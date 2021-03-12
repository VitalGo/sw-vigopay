package com.othr.swvigopay.controller;

import com.othr.swvigopay.entity.Account;
import com.othr.swvigopay.entity.Transfer;
import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.*;
import com.othr.swvigopay.service.AccountServiceIF;
import com.othr.swvigopay.service.TransferServiceIF;
import com.othr.swvigopay.service.UserServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@Controller
@RequestMapping("/transfers")
public class TransferController {

    @Autowired
    UserServiceIF userServiceIF;
    @Autowired
    AccountServiceIF accountServiceIF;
    @Autowired
    TransferServiceIF transferServiceIF;

    @RequestMapping("/list")
    public String transfersPage(Model model,
                                @RequestParam(value = "successMessage", required = false) String successMessage,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "pageReq", required = false, defaultValue = "1") int pageReq,
                                @RequestParam(value = "pageDone", required = false, defaultValue = "1") int pageDone) {

        if(successMessage != null) {
            model.addAttribute("successIsVisible", true);
            model.addAttribute("successText", successMessage);
        }

        if(error != null) {
            model.addAttribute("alertRRIsVisible", true);
            model.addAttribute("alertRRText", error);
        }

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("currentUserEmail", currentUser.getEmail());

        Page<Transfer> transferDonePage;
        Page<Transfer> transferRequestedPage;

        // generate a page of processed transfers
        Pageable pageableDone = PageRequest.of(pageDone - 1, 4, Sort.by("processedTime").descending());
        transferDonePage = transferServiceIF.queryProcessedTransfer(currentUser.getAccount().getId(), pageableDone);

        // generate a page of requested transfers
        Pageable pageableRequest = PageRequest.of(pageReq - 1, 4, Sort.by("requestTime").descending());
        transferRequestedPage = transferServiceIF.queryRequestedTransfers(currentUser.getAccount().getId(), pageableRequest);

        model.addAttribute("currentPageDone", pageDone);
        model.addAttribute("totalPagesDone", transferDonePage.getTotalPages());

        model.addAttribute("currentPageReq", pageReq);
        model.addAttribute("totalPagesReq", transferRequestedPage.getTotalPages());

        // dont show the requested transfers section if the list is empty
        if(transferRequestedPage.isEmpty())
            model.addAttribute("requestedTransfersDiv", false);
        else
            model.addAttribute("requestedTransfersDiv", true);

        // dont show the processed transfers section if the list is empty
        if(transferDonePage.isEmpty())
            model.addAttribute("doneTransfersDiv", false);
        else
            model.addAttribute("doneTransfersDiv", true);

        // if the account have no transfers yet, show a info message
        if(transferDonePage.isEmpty() && transferRequestedPage.isEmpty())
            model.addAttribute("showTransfersInfo", true);
        else
            model.addAttribute("showtransfersInfo", false);

        model.addAttribute("transferDone", transferDonePage);
        model.addAttribute("transferRequested", transferRequestedPage);

        return "transfers";
    }

    @RequestMapping("/send")
    public String makeTransferPage(Model model,
                                   @ModelAttribute("email") String email) {

        model.addAttribute("searchForReceiver", true);

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        return "makeTransfer";
    }

    @RequestMapping("/sendTo")
    public String makeTransferToPage(Model model,
                                     @RequestParam("email") String email,
                                     @RequestParam(value = "message", required = false) String message) {

        model.addAttribute("searchForReceiver", true);

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User receiverUser;

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        if(message != null) {
            model.addAttribute("warningIsVisible", true);
            model.addAttribute("warningText", message);
        }

        // check if the receiver email is valid
        try {
            receiverUser = userServiceIF.getUserByEmail(email);

            if(email.equals(currentUser.getEmail())) {
                model.addAttribute("alertIsVisible", true);
                model.addAttribute("alertText", "Sie können kein Geld an sich selbst senden!");
            } else if(!receiverUser.isActive()) {
                model.addAttribute("alertIsVisible", true);
                model.addAttribute("alertText", "Es existiert kein Nutzer mit dieser E-Mail-Adresse!");
            } else {
                model.addAttribute("searchForReceiver", false);
                model.addAttribute("sendTo", true);
                model.addAttribute("sendToEmail", email);
                model.addAttribute("sendToName", receiverUser.generateUsername());
            }
        } catch (UserNotFoundException e) {
            model.addAttribute("alertIsVisible", true);
            model.addAttribute("alertText", "Es existiert kein Nutzer mit dieser E-Mail-Adresse!");
        }


        return "makeTransfer";
    }

    @PostMapping("/sendTo")
    public RedirectView createNewTransfer(Model model,
                                          @RequestParam("email") String email,
                                          @ModelAttribute("euro") int euro,
                                          @ModelAttribute("cent") int cent,
                                          @ModelAttribute("description") String description) throws TransferServiceException, BankingServiceException, UserNotFoundException {

        model.addAttribute("searchForReceiver", true);

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account currentAccount;

        // get current users account
        try {
            currentAccount = accountServiceIF.getAccountByUserId(currentUser.getId());
        } catch (Exception e) {
            return new RedirectView("/transfers/sendTo?email=" + email + "&message=Etwas ist schief gelaufen! Versuchen Sie es bitte später erneut.");
        }

        // get receiver account
        Account receiverAccount = userServiceIF.getUserByEmail(email).getAccount();

        String amountString = String.format("%d", euro) + "." + String.format("%02d", cent);
        BigDecimal amount = new BigDecimal(amountString);

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        // generate a new transfer object
        Transfer transfer = new Transfer();
        transfer.setPayerAccount(currentAccount);
        transfer.setReceiverAccount(receiverAccount);
        transfer.setAmount(amount);
        transfer.setRequestTime(null);
        transfer.setProcessedTime(null);
        transfer.setDescription(description);

        // make a new transfer
        try {
            transferServiceIF.makeTransfer(transfer);
        } catch(BankingServiceException e) {
            return new RedirectView("/transfers/sendTo?email=" + email + "&message=Der fehlende Betrag konnte leider nicht von Ihrem Bankkonto gebucht werden. Versuchen Sie es spaeter erneut oder kontaktieren Sie den Support.");
        } catch(NegativeAmountExceptions e) {
            return new RedirectView("/transfers/sendTo?email=" + email + "&message=Sie können keine negativen Beträge senden.");
        }

        return new RedirectView("/transfers/list?successMessage=Der Betrag von " + amountString + " EUR wurde erfolgreich an " + receiverAccount.getUser().generateUsername() + " transferiert.");
    }

    @RequestMapping("/request")
    public String requestTransferPage(Model model,
                                   @ModelAttribute("email") String email) {
        model.addAttribute("alertIsVisible", false);
        model.addAttribute("requestFrom", false);
        model.addAttribute("searchForPayer", true);

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        return "requestTransfer";
    }

    @RequestMapping("/requestFrom")
    public String requestTransferFromPage(Model model,
                                     @RequestParam("email") String email) {
        model.addAttribute("alertIsVisible", false);
        model.addAttribute("requestFrom", false);
        model.addAttribute("searchForPayer", true);

        User payerUser;
        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        // check if the payer email is valid
        try {
            payerUser = userServiceIF.getUserByEmail(email);

            if (email.equals(currentUser.getEmail())) {
                model.addAttribute("alertIsVisible", true);
                model.addAttribute("alertText", "Sie können sich selbst keine Anfrage senden!");
            } else if (!payerUser.isActive()) {
                model.addAttribute("alertIsVisible", true);
                model.addAttribute("alertText", "Es existiert kein Nutzer mit dieser E-Mail-Adresse!");
            } else {
                model.addAttribute("searchForPayer", false);
                model.addAttribute("requestFrom", true);
                model.addAttribute("requestFromEmail", email);
                model.addAttribute("requestFromName", payerUser.generateUsername());

            }
        } catch (UserNotFoundException e) {
            model.addAttribute("alertIsVisible", true);
            model.addAttribute("alertText", "Es existiert kein Nutzer mit dieser E-Mail-Adresse!");
        }

        return "requestTransfer";
    }

    @PostMapping("/requestFrom")
    public RedirectView requestNewTransfer(Model model,
                                          @RequestParam("email") String email,
                                          @ModelAttribute("euro") int euro,
                                          @ModelAttribute("cent") int cent,
                                          @ModelAttribute("description") String description) throws TransferServiceException, UserNotFoundException {
        model.addAttribute("alertIsVisible", false);
        model.addAttribute("requestFrom", false);
        model.addAttribute("searchForReceiver", true);

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // get current users account
        Account currentAccount = currentUser.getAccount();
        Account payerAccount;

        String amountString = String.format("%d", euro) + "." + String.format("%02d", cent);
        BigDecimal amount = new BigDecimal(amountString);

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        payerAccount = userServiceIF.getUserByEmail(email).getAccount();

        // generate a new transfer object
        Transfer transfer = new Transfer();
        transfer.setPayerAccount(payerAccount);
        transfer.setReceiverAccount(currentAccount);
        transfer.setAmount(amount);
        transfer.setRequestTime(null);
        transfer.setProcessedTime(null);
        transfer.setDescription(description);
        // request a new transfer
        transferServiceIF.requestTransfer(transfer);

        return new RedirectView("/transfers/list?successMessage=" + "Sie haben soeben erfolgreich eine Geldanfrage an " + payerAccount.getUser().generateUsername() + " gesendet.");
    }

    @PostMapping("/request/accept/{id}")
    public RedirectView acceptRequestedTransfer(@PathVariable long id) {

        // try to get the transfer by id and accept it
        try {
            Transfer transfer = transferServiceIF.getTransferById(id);
            try {
                transferServiceIF.acceptRequestedTransfer(transfer);
            } catch (BankingServiceException e) {
                return new RedirectView("/transfers/list?error=Der fehlende Betrag konnte leider nicht von Ihrem Bankkonto gebucht werden. Versuchen Sie es spaeter erneut oder kontaktieren Sie den Support.");
            } catch (NegativeAmountExceptions e) {
                return new RedirectView("/transfers/list?error=Etwas ist schief gelaufen! Bitte kontaktieren Sie den Support.");
            }
        } catch (TransferServiceException e) {
            return new RedirectView("/transfers/list?error=Etwas ist schief gelaufen! Bitte kontaktieren Sie den Support.");
        }

        return new RedirectView("/transfers/list");
    }

    @PostMapping("/request/decline/{id}")
    public RedirectView declineRequestedTransfer(@PathVariable long id) throws TransferServiceException {

        // try to get the transfer by id and decline it
        try {
            Transfer transfer = transferServiceIF.getTransferById(id);
            transferServiceIF.declineRequestedTransfer(transfer);
        } catch (TransferServiceException e) {
            return new RedirectView("/transfers/list?error=Etwas ist schief gelaufen! Bitte kontaktieren Sie den Support.");
        }

        return new RedirectView("/transfers/list");
    }
}
