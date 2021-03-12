package com.othr.swvigopay.controller;

import com.othr.swvigopay.entity.User;
import com.othr.swvigopay.service.UserServiceIF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    UserServiceIF userServiceIF;

    @RequestMapping(value={"/", "/login"})
    public String showStartPage(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "message", required = false) String message,
                                @RequestParam(value = "createdAccount", required = false) String createdAccount,
                                @RequestParam(value = "accountDeleted", required = false) String accountDeleted) {
        User user = new User();
        model.addAttribute("user", user);

        if (error != null) {
            model.addAttribute("warningIsVisible", true);
            model.addAttribute("warningText", "Benutzer E-Mail und Passwort stimmen nicht überein!");
        }

        if (logout != null || createdAccount != null || message != null) {
            model.addAttribute("successIsVisible", true);

            if (logout != null)
                model.addAttribute("successText", "Sie wurden erfolgreich ausgeloggt!");
            else if (createdAccount != null)
                model.addAttribute("successText", "Sie haben erfolgreich einen Account erstellt!");
            else if (message != null)
                model.addAttribute("successText", message);
        }

        return "login";
    }
}
