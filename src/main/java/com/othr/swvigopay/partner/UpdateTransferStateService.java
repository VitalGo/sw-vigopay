package com.othr.swvigopay.partner;

import com.othr.swvigopay.entity.TransferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UpdateTransferStateService {

    @Autowired
    private RestTemplate restServiceClient;

    @Autowired
    @Qualifier("statusUpdate")
    private HttpHeaders statusUpdateHeader;

    public void transferStateUpdateNotificationChat(TransferDTO transferDTO) {
        HttpEntity<TransferDTO> request = new HttpEntity<TransferDTO>(transferDTO, statusUpdateHeader);

        restServiceClient.exchange("http://im-codd.oth-regensburg.de:8941/api/callback/vigopay/transfers/stateUpdate", HttpMethod.POST, request, HttpStatus.class);
    }

//    public void transferStateUpdateNotificationWebshop(TransferDTO transferDTO) {
//        HttpEntity<TransferDTO> request = new HttpEntity<TransferDTO>(transferDTO, statusUpdateHeader);
//
//        restServiceClient.exchange("http://jonas-webshop/api/callback/vigopay/transfers/stateUpdate", HttpMethod.POST, request, HttpStatus.class);
//    }
}
