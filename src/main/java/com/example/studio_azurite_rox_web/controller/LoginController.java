package com.example.studio_azurite_rox_web.controller;

import com.example.studio_azurite_rox_web.Utility.ErrorMessageGenerator;
import com.example.studio_azurite_rox_web.dto.StudioMemberForm;
import com.example.studio_azurite_rox_web.service.StudioMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class LoginController {
    @Autowired
    StudioMemberService studioMemberService;
    @Autowired
    ErrorMessageGenerator errorMessageGenerator;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("@login")
    public String loginPost() {
        return "login";
    }

    @GetMapping("/new_registration_form")
    public String newRegistration(Model model, StudioMemberForm studioMemberForm) {
        model.addAttribute("studioMemberForm", studioMemberForm);

        return "new_registration_form";
    }

    @PostMapping("/new_registration_form")
    public String newRegistration(@Validated @ModelAttribute StudioMemberForm studioMemberForm,
                                  BindingResult bindingResult,
                                  Model model) {
/*
        log.info("\n" +
                 studioMemberForm.getName() + "\n" +
                studioMemberForm.getFurigana() + "\n" +
                 studioMemberForm.getAddress() + "\n" +
                 studioMemberForm.getPhone() + "\n" +
                 studioMemberForm.getEmail() + "\n" +
                 studioMemberForm.getArtistName() + "\n" +
                 studioMemberForm.getMemberCount() + "\n" +
                 studioMemberForm.getNote());
*/

        if (bindingResult.hasErrors()) {
            return "new_registration_form";
        }

        // 会員情報の保存
        try {
            studioMemberService.insertData(studioMemberForm);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.info("\n" + e.getMessage());

            // エラー表示
//            model.addAttribute("exceptionError", true);
            errorMessageGenerator.getErrorMessage(model, "00-01-00-11");

            return "new_registration_form";
        }

        return "redirect:/page?cn=new_registration_ok";
    }
}
