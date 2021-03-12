package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.Account;
import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.exceptions.TransferServiceException;
import de.othr.sw.bank.service.AccountNotFoundException;

public interface AccountServiceIF {

        void createNewAccount(User user);

        Account updateAccount(Account account);

        void deleteAccount(Account account) throws TransferServiceException, com.othr.swvigopay.exceptions.AccountNotFoundException;

        Account getAccountByAccountId(long id) throws com.othr.swvigopay.exceptions.AccountNotFoundException;

        Account getAccountByUserId(long id) throws AccountNotFoundException, com.othr.swvigopay.exceptions.AccountNotFoundException;
}
