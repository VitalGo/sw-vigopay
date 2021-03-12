package com.othr.swvigopay.utils;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler
{

    // this custom logout handler can pass messages to the login screen
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        if(request.getParameter("message") != null)
            response.sendRedirect(request.getContextPath()+"/login?message=" + request.getParameter("message"));
        else
            response.sendRedirect(request.getContextPath() + "/login?logout");
    }
}