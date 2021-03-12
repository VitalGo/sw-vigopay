package com.othr.swvigopay.controller;

import com.othr.swvigopay.entity.TransferDTO;
import com.othr.swvigopay.exceptions.TransferServiceExternalException;
import com.othr.swvigopay.service.TransferServiceExternalIF;
import com.othr.swvigopay.utils.AuthTokenUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferServiceRestController {

    @Autowired
    TransferServiceExternalIF transferServiceExternalIF;
    @Autowired
    AuthTokenUtilities authTokenUtilities;

    @PostMapping("/requestTransferExternal")
    public ResponseEntity<TransferDTO> requestTransferExternal(@RequestBody TransferDTO transferDTO, @RequestHeader("access-token") String accessToken) {
        // check if the access-token is valid
        if(!authTokenUtilities.checkIfTokenIsValid(accessToken))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        try {
            return transferServiceExternalIF.requestTransferExternal(transferDTO);
        } catch (TransferServiceExternalException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
