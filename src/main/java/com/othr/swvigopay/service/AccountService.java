package com.othr.swvigopay.service;

import com.othr.swvigopay.exceptions.AccountNotFoundException;
import de.othr.sw.bank.entity.TransferRequest;
import de.othr.sw.bank.service.BankingServiceExternalIF;
import com.othr.swvigopay.entity.Account;
import com.othr.swvigopay.entity.Transfer;
import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.TransferServiceException;
import com.othr.swvigopay.repository.AccountRepositoryIF;
import com.othr.swvigopay.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@RestController
public class AccountService implements AccountServiceIF {

    @Autowired
    private AccountRepositoryIF accountRepositoryIF;
    @Autowired
    private UserServiceIF userServiceIF;
    @Autowired
    private TransferServiceIF transferServiceIF;
    @Autowired
    private BankingServiceExternalIF bankingServiceIF;
    @Value("${vigopay-iban}")
    private String vigopayIban;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNewAccount(User user) {
        // create a new user
        User createdUser = userServiceIF.createNewUser(user);
        Account inactiveAccount;

        // check if the user may have an inactive account
        try {
            // if true, set account to active
            inactiveAccount = getAccountByUserId(createdUser.getId());
            inactiveAccount.setActive(true);
            inactiveAccount.setBalance(new BigDecimal(500.00));
            accountRepositoryIF.save(inactiveAccount);
        } catch (AccountNotFoundException e) {
            // if false, create new account
            accountRepositoryIF.save(new Account(createdUser));
        }
    }

    @Override
    public Account updateAccount(Account account) {
        return accountRepositoryIF.save(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Account account) throws TransferServiceException, AccountNotFoundException {
        ResponseEntity<TransferRequest> transferResponse;

        Account userAccount = getAccountByAccountId(account.getId());

        // check if the account balance is positive
        if(userAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            TransferRequest transferRequest = new TransferRequest();

            // generade a transferRequest for the Banking Service (partner project)
            transferRequest.setReceiverForename(userAccount.getUser().getForename());
            transferRequest.setReceiverSurname(userAccount.getUser().getSurname());
            transferRequest.setIban(vigopayIban);
            transferRequest.setReceiverIban(userAccount.getUser().getIban());
            transferRequest.setAmount(userAccount.getBalance().movePointRight(2).longValueExact());
            transferRequest.setDescription(UUID.randomUUID().toString());

            // transfer money from Vigopay bank account to users bank account
            try {
                transferResponse = bankingServiceIF.transferMoney(transferRequest);
                if(transferResponse.getStatusCode() != HttpStatus.CREATED)
                    throw new TransferServiceException("Could not tranfer money to the bank account");

                userAccount.setBalance(BigDecimal.ZERO);
            } catch (Exception e) {
                throw new TransferServiceException("Could not tranfer money to the bank account");
            }
        }

        // delete all requested transfers where this account is the payer
        for (Transfer transfer : userAccount.getPayerTransfers()) {
            if(transfer.getState() == State.REQUESTED)
                transferServiceIF.deleteTransfer(transfer);
        }

        // delete all requested transfers where this account is the receiver
        for (Transfer transfer : userAccount.getReceiverTransfers()) {
            if(transfer.getState() == State.REQUESTED)
                transferServiceIF.deleteTransfer(transfer);
        }

        // set account state to inactive
        userAccount.setActive(false);
        updateAccount(userAccount);

        // set user state to inactive
        userAccount.getUser().setActive(false);
        userServiceIF.updateUser(userAccount.getUser());
    }

    @Override
    public Account getAccountByAccountId(long id) throws AccountNotFoundException {
        Account account = accountRepositoryIF.findById(id);
        if(account != null)
            return account;
        throw new AccountNotFoundException("An account with the User-ID " + id + " does not exist!");
    }

    @Override
    public Account getAccountByUserId(long id) throws AccountNotFoundException {
        Account account = accountRepositoryIF.findByUserId(id);
        if(account != null)
            return account;
        throw new AccountNotFoundException("An account with the User-ID " + id + " does not exist!");
    }
}
