package com.othr.swvigopay.partner;

import de.othr.sw.bank.entity.TransferRequest;
import de.othr.sw.bank.service.AccountNotFoundException;
import de.othr.sw.bank.service.BankingServiceExternalIF;
import de.othr.sw.bank.service.InvalidTransferException;
import de.othr.sw.bank.service.NotEnoughMoneyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class BankingService implements BankingServiceExternalIF {

    @Autowired
    private RestTemplate restServiceClient;

    @Autowired
    @Qualifier("banking")
    private HttpHeaders bankingHeader;

    @Override
    public ResponseEntity<TransferRequest> transferMoney(TransferRequest transferRequest) throws AccountNotFoundException, InvalidTransferException, NotEnoughMoneyException {
        HttpEntity<TransferRequest> request = new HttpEntity<TransferRequest>(transferRequest, bankingHeader);

        ResponseEntity<TransferRequest> response;
        response = restServiceClient.exchange("http://im-codd.oth-regensburg.de:8915/api/banking/transfers/transfer", HttpMethod.POST, request, TransferRequest.class);
        return response;
    }

    @Override
    public ResponseEntity<TransferRequest> mandateMoney(TransferRequest transferRequest) throws AccountNotFoundException, InvalidTransferException, NotEnoughMoneyException {
        System.out.println(bankingHeader.toString());
        HttpEntity<TransferRequest> request = new HttpEntity<TransferRequest>(transferRequest, bankingHeader);

        ResponseEntity<TransferRequest> response;
        response = restServiceClient.exchange("http://im-codd.oth-regensburg.de:8915/api/banking/transfers/mandate", HttpMethod.POST, request, TransferRequest.class);
        return response;
    }
}