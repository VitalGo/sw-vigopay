package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.Transfer;
import com.othr.swvigopay.exceptions.BankingServiceException;
import com.othr.swvigopay.exceptions.NegativeAmountExceptions;
import com.othr.swvigopay.exceptions.TransferServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferServiceIF extends TransferServiceExternalIF {

    Transfer makeTransfer(Transfer transfer) throws BankingServiceException, TransferServiceException, NegativeAmountExceptions;

    Transfer requestTransfer(Transfer transfer) throws TransferServiceException;

    Transfer acceptRequestedTransfer(Transfer transfer) throws BankingServiceException, NegativeAmountExceptions;

    Transfer declineRequestedTransfer(Transfer transfer) throws TransferServiceException;

    void deleteTransfer(Transfer transfer);

    Transfer getTransferById(long id) throws TransferServiceException;

    Page<Transfer> queryProcessedTransfer(long accountId, Pageable pageable);

    Page<Transfer> queryRequestedTransfers(long accountId, Pageable pageable);
}
