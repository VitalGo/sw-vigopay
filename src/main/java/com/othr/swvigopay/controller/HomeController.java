package com.othr.swvigopay.controller;

import com.othr.swvigopay.entity.Account;
import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.service.AccountServiceIF;
import com.othr.swvigopay.service.UserServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;

@Controller
public class HomeController {

    @Autowired
    UserServiceIF userServiceIF;
    @Autowired
    AccountServiceIF accountServiceIF;

    @RequestMapping("/home")
    public String showHomePage(Model model) {
        DecimalFormat df = new DecimalFormat("#.##");

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account currentAccount;

        // try to get the current users account
        try {
            currentAccount = accountServiceIF.getAccountByUserId(currentUser.getId());
        } catch (Exception e) {
            return "redirect:/logout?message=Etwas ist schief gelaufen! Sie wurden automatisch ausgeloggt.";
        }

        model.addAttribute("currentUserEmail", currentUser.getEmail());

        // show account data
        model.addAttribute("username", currentUser.generateUsername());

        if(currentAccount.getBalance().signum() < 0) {
            model.addAttribute("balanceStyle", "font-size: 28px; color: red;");
            model.addAttribute("balance", currentAccount.getBalance());
        } else {
            model.addAttribute("balanceStyle", "font-size: 28px; color: springgreen;");
            model.addAttribute("balance", "+" + currentAccount.getBalance());
        }

        model.addAttribute("iban", currentUser.getIban());

        return "home";
    }

}
