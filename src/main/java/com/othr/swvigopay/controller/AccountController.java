package com.othr.swvigopay.controller;

import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.AccountNotFoundException;
import com.othr.swvigopay.exceptions.TransferServiceException;
import com.othr.swvigopay.service.AccountServiceIF;
import com.othr.swvigopay.service.UserServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AccountController {

    @Autowired
    AccountServiceIF accountServiceIF;
    @Autowired
    UserServiceIF userServiceIF;

    @RequestMapping("/createAccount")
    public String showCreateAccountPage(Model model, @ModelAttribute("user") User user) {

        model.addAttribute("alertIsVisible", false);
        return "createAccount";
    }

    @PostMapping("/createAccount")
    public String createAccount(Model model, @ModelAttribute("user") User user) {

        // check if there is always a user registrated with this email
        if(!userServiceIF.checkIfEmailIsAvailable(user.getEmail())) {
            model.addAttribute("warningIsVisible", true);
            model.addAttribute("warningText", "Es existiert bereits ein Benutzer mit dieser E-Mail Adresse!");

            return "createAccount";
        } else {
            user.setIban(user.getIban().replaceAll("\\s",""));
            accountServiceIF.createNewAccount(user);

            return "redirect:/login?createdAccount";
        }
    }

    // confirmation page
    @RequestMapping("/deleteAccount")
    public String showDeleteAccountPage(Model model, @RequestParam(value = "message", required = false) String message) {

        if(message != null){
            model.addAttribute("alertIsVisible", true);
            model.addAttribute("alertText", message);
        }

        return "deleteAccount";
    }

    @PostMapping("/deleteAccount")
    public RedirectView deleteAccount() {

        // get current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // delete the account and transfer his remaining amount to his bank account
        try {
            accountServiceIF.deleteAccount(currentUser.getAccount());
        } catch (TransferServiceException e) {
            return new RedirectView("deleteAccount?message=Ihr restlicher Kontostand konnte nicht auf Ihr Bankkonto transferiert werden. Bitte wenden Sie sich an den Support.");
        } catch (AccountNotFoundException e) {
            return new RedirectView("deleteAccount?message=Es ist ein Problem aufgetreten! Bitte versuchen Sie es später erneut.");
        }

        return new RedirectView("/logout?message=Ihr Account wurde erfolgreich geloescht und das restliche Guthaben wurde auf Ihr Bankkonto ueberwiesen.");
    }
}