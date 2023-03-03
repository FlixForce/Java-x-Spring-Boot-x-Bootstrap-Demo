package com.example.studio_azurite_rox_web.controller;

import com.example.studio_azurite_rox_web.dto.MailForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/page")
    public String page(@RequestParam("cn") String requestParam,
                       Model model) {
        requestParam = "content/" + requestParam;
        model.addAttribute("contentName", requestParam);

        return "page";
    }

    @GetMapping("/mail_form")
    public String mailForm(Model model, MailForm mailForm) {
        model.addAttribute("mailForm", mailForm);

        return "mail_form";
    }

    @PostMapping("/mail_form")
    public String mailForm(@Validated @ModelAttribute MailForm mailForm,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "mail_form";
        }

        return "redirect:/page?cn=mail_ok";
    }
}
