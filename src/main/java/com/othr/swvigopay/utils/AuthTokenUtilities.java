package com.othr.swvigopay.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthTokenUtilities {

    private String validToken;

    public AuthTokenUtilities() {
        this.validToken = "kWxqZGRpbjpvctVuIHNlc2FtZQ=(e";
    }

    public boolean checkIfTokenIsValid(String token) {
        return token != null && validToken.equals(token);
    }

    @Bean
    public AuthTokenUtilities tokenUtil() {
        return new AuthTokenUtilities();
    }
}
