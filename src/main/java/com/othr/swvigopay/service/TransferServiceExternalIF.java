package com.othr.swvigopay.service;

import com.othr.swvigopay.entity.TransferDTO;
import com.othr.swvigopay.exceptions.TransferServiceExternalException;
import org.springframework.http.ResponseEntity;

public interface TransferServiceExternalIF {

    ResponseEntity<TransferDTO> requestTransferExternal(TransferDTO transferDTO) throws TransferServiceExternalException;
}
