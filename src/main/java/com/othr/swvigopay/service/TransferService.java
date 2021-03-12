package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.*;
import com.othr.swvigopay.exceptions.BankingServiceException;
import com.othr.swvigopay.exceptions.TransferServiceExternalException;
import com.othr.swvigopay.partner.UpdateTransferStateService;
import de.othr.sw.bank.entity.TransferRequest;
import de.othr.sw.bank.service.BankingServiceExternalIF;
import com.othr.swvigopay.exceptions.NegativeAmountExceptions;
import com.othr.swvigopay.exceptions.TransferServiceException;
import com.othr.swvigopay.repository.TransferRepositoryIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service
@RestController
public class TransferService implements TransferServiceIF {

    @Autowired
    TransferRepositoryIF transferRepositoryIF;
    @Autowired
    AccountServiceIF accountServiceIF;
    @Autowired
    UserServiceIF userServiceIF;
    @Autowired
    UpdateTransferStateService updateTransferStateService;
    @Autowired
    BankingServiceExternalIF bankingServiceIF;
    @Value("${vigopay-iban}")
    private String vigopayIban;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transfer makeTransfer(Transfer transfer) throws NegativeAmountExceptions, BankingServiceException {

        Date date = new Date();
        Account payerAccount = transfer.getPayerAccount();
        Account receiverAccount = transfer.getReceiverAccount();
        ResponseEntity<TransferRequest> transferResponse;

        // check if the transfer amout is negative or null
        if (transfer.getAmount().signum() <= 0)
            throw new NegativeAmountExceptions("No negative amounts allowed.");

        payerAccount.setBalance(payerAccount.getBalance().subtract(transfer.getAmount()));

        // check if the new account balance is negative
        if (payerAccount.getBalance().signum() < 0) {
            TransferRequest transferRequest = new TransferRequest();

            // generade a transferRequest for the Banking Service (partner project)
            transferRequest.setReceiverForename(payerAccount.getUser().getForename());
            transferRequest.setReceiverSurname(payerAccount.getUser().getSurname());
            transferRequest.setReceiverIban(payerAccount.getUser().getIban());
            transferRequest.setIban(vigopayIban);
            transferRequest.setAmount((payerAccount.getBalance().negate().movePointRight(2).longValueExact()));
            transferRequest.setDescription(UUID.randomUUID().toString());

            // transfer money from payers bank account to Vigopay bank account
            try {
                transferResponse = bankingServiceIF.mandateMoney(transferRequest);
                if(transferResponse.getStatusCode() != HttpStatus.CREATED)
                    throw new BankingServiceException("Could not transfert money from " + payerAccount.getUser().getEmail() + " to Vigopay account.");

                payerAccount.setBalance(BigDecimal.ZERO);
            } catch (Exception e) {
                throw new BankingServiceException("Could not transfert money from " + payerAccount.getUser().getEmail() + " to Vigopay account.");
            }
        }

        // update the account balance of payer and receiver
        payerAccount = accountServiceIF.updateAccount(payerAccount);
        receiverAccount.setBalance(receiverAccount.getBalance().add(transfer.getAmount()));
        receiverAccount = accountServiceIF.updateAccount(receiverAccount);

        // set the state and the timestamp of the transfer and update or save it
        transfer.setProcessedTime(new Timestamp(date.getTime()));
        transfer.setState(State.DONE);
        transferRepositoryIF.save(transfer);

        return transfer;
    }

    @Override
    public Transfer requestTransfer(Transfer transfer) {
        Date date = new Date();

        // create a new transfer with the state REQUESTED and the request Timestamp and safe it
        transfer.setRequestTime(new Timestamp(date.getTime()));
        transfer.setState(State.REQUESTED);

        transfer = transferRepositoryIF.save(transfer);

        return transfer;
    }

    @Override
    public Transfer acceptRequestedTransfer(Transfer transfer) throws BankingServiceException, NegativeAmountExceptions {

        // execute the requested transfer
        Transfer newTransfer = makeTransfer(transfer);

        // check if the transfer were requested from a external source
        // if true, send a state update notification to this source
        if(transfer.isFromExternal()) {
            // check if the requested transfer was made by the chat service
            if(transfer.getDescription().contains("&&&chat&&&")) {
                try {
                    TransferDTO transferDTO = new TransferDTO(newTransfer.getPayerAccount().getUser().getEmail(), newTransfer.getReceiverAccount().getUser().getEmail(), newTransfer.getAmount(), newTransfer.getDescription());
                    transferDTO.setState(newTransfer.getState());
                    updateTransferStateService.transferStateUpdateNotificationChat(transferDTO);
                } catch (Exception e) {
                    System.out.println("Something went wrong by notifying the enquirer");
                }
            }
        }
        return newTransfer;
    }


    @Override
    public Transfer declineRequestedTransfer(Transfer transfer) {
        Date date = new Date();

        // change the state and set a new timestamp and save the transfer
        transfer.setProcessedTime(new Timestamp(date.getTime()));
        transfer.setState(State.DECLINED);

        transfer = transferRepositoryIF.save(transfer);

        // check if the transfer were requested from a external source
        // if true, send a state update notification to this source
        if(transfer.isFromExternal()) {
            // check if the requested transfer was made by the chat service
            if(transfer.getDescription().contains("&&&chat&&&")) {
                try {
                    TransferDTO transferDTO = new TransferDTO(transfer.getPayerAccount().getUser().getEmail(), transfer.getReceiverAccount().getUser().getEmail(), transfer.getAmount(), transfer.getDescription());
                    transferDTO.setState(transfer.getState());
                    updateTransferStateService.transferStateUpdateNotificationChat(transferDTO);
                } catch (Exception e) {
                    System.out.println("Something went wrong by notifying the enquirer");
                }
            }
        }
        return transfer;
    }

    @Override
    public void deleteTransfer(Transfer transfer) {
        transferRepositoryIF.delete(transfer);
    }

    @Override
    public Transfer getTransferById(long id) throws TransferServiceException {
        Transfer transfer = transferRepositoryIF.findById(id);

        if(transfer == null)
            throw new TransferServiceException("Could not find a transfer with the id " + id);

        return transfer;
    }

    @Override
    public Page<Transfer> queryProcessedTransfer(long accountId, Pageable pageable) {
        // generate a page of processed transfers
        Page<Transfer> transfers =  transferRepositoryIF.findByPayerAccountIdAndStateIsNotOrReceiverAccountIdAndStateIsNot(accountId, State.REQUESTED, accountId, State.REQUESTED, pageable);
        return transfers;
    }

    @Override
    public Page<Transfer> queryRequestedTransfers(long accountId, Pageable pageable) {
        // generate a page of requested transfers
        Page<Transfer> transfers =  transferRepositoryIF.findByPayerAccountIdAndStateOrReceiverAccountIdAndState(accountId, State.REQUESTED, accountId, State.REQUESTED, pageable);
        return transfers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<TransferDTO> requestTransferExternal(TransferDTO transferDTO) throws TransferServiceExternalException {
        Transfer transfer = new Transfer();

        User payerUser;
        User receiverUser;
        Account payerAccount;
        Account receiverAccount;

        // check if the inputs are valid
        if(!(transferDTO.getAmount() instanceof BigDecimal) || transferDTO.getAmount().signum() <= 0 || transferDTO.getPayerEmail().equals("") || transferDTO.getReceiverEmail().equals("")) {
            throw new TransferServiceExternalException("Invalid transfer values.");
        }

        // get the payer and receiver accounts
        try {
            payerUser = userServiceIF.getUserByEmail(transferDTO.getPayerEmail());
            receiverUser = userServiceIF.getUserByEmail(transferDTO.getReceiverEmail());

            if(!payerUser.isActive())
                throw new TransferServiceExternalException("Payer user with the email " + payerUser.getEmail() + " is inactive.");
            if(!receiverUser.isActive())
                throw new TransferServiceExternalException("Receiver user with the email " + receiverUser.getEmail() + " is inactive.");

            payerAccount = accountServiceIF.getAccountByUserId(payerUser.getId());
            receiverAccount = accountServiceIF.getAccountByUserId(receiverUser.getId());
        } catch (Exception e) {
            throw new TransferServiceExternalException("Payer " + transferDTO.getPayerEmail() + " or receiver " + transferDTO.getReceiverEmail() + " does not exist.");
        }

        // generate a new transfer object
        transfer.setPayerAccount(payerAccount);
        transfer.setReceiverAccount(receiverAccount);
        transfer.setAmount(transferDTO.getAmount());
        transfer.setDescription(transferDTO.getDescription());
        transfer.setFromExternal(true);

        // request a transfer
        transfer = requestTransfer(transfer);

        transferDTO.setState(State.REQUESTED);
        return new ResponseEntity<>(transferDTO, HttpStatus.CREATED);
    }
}