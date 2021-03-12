package com.othr.swvigopay.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;

@Configuration
public class AuthHeaderUtilities {

    // generate a HttpHeader including the access-token for the Banking Service (partner project)
    @Bean
    @Qualifier("banking")
    @Scope(scopeName = "singleton")
    public HttpHeaders bankingTokenHeader() {
        HttpHeaders header = new HttpHeaders();
        header.add("access-token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiVmlHb19QYXkiLCJqdGkiOiI4NDZmOTM0My1kMjdmLTQ2OGMtOWE1Yy1mNDA2MTgyMmM1ZTIiLCJpYXQiOjE2MTA3MDU3NzYsImV4cCI6MTYxMDcwOTM3Nn0.5uJnQkQLVOfbIybnsgLkNaK5Sf0lOEdMtllKyjWx42Y");
        return new HttpHeaders(header);
    }

    // generate a HttpHeader including the access-token for the state update service
    @Bean
    @Qualifier("statusUpdate")
    @Scope(scopeName = "singleton")
    public HttpHeaders statusUpdateTokenHeader() {
        HttpHeaders header = new HttpHeaders();
        header.add("access-token", "kWxqZGRpbjpvctVuIHNlc2FtZQ=(e");
        return new HttpHeaders(header);
    }
}
