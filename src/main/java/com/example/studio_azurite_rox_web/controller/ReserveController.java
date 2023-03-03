package com.example.studio_azurite_rox_web.controller;

import com.example.studio_azurite_rox_web.Utility.ErrorMessageGenerator;
import com.example.studio_azurite_rox_web.dto.ReserveForm;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.service.ReserveInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Calendar;

@Slf4j
@Controller
public class ReserveController {
    @Autowired
    ReserveInfoService reserveInfoService;
    @Autowired
    ErrorMessageGenerator errorMessageGenerator;

    private ReserveForm getDefaultReserveDateTime(ReserveForm reserveForm) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        String dateTime = String.format("%04d-%02d-%02dT10:00",
                calendar.get(Calendar.YEAR),
                (calendar.get(Calendar.MONTH) + 1),
                calendar.get(Calendar.DATE));
        reserveForm.setStartDatetime(dateTime);
        dateTime = String.format("%04d-%02d-%02dT11:00",
                calendar.get(Calendar.YEAR),
                (calendar.get(Calendar.MONTH) + 1),
                calendar.get(Calendar.DATE));
        reserveForm.setEndDatetime(dateTime);

        return reserveForm;
    }

    @GetMapping("/reserve_form")
    public String reserveForm(Model model, ReserveForm reserveForm) {
        try {
            reserveForm = getDefaultReserveDateTime(reserveForm);
            model.addAttribute("reserveForm", reserveForm);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        return "reserve_form";
    }

    @PostMapping("/reserve_form")
    public String reserveForm(@Validated @ModelAttribute ReserveForm reserveForm,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal LoginUserDetails loginUserDetails,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "reserve_form";
        }

        // 予約情報の保存
        try {
            reserveInfoService.insertData(loginUserDetails, reserveForm);
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "03-01-00-16");

            return "reserve_form";
        }

        return "redirect:/page?cn=reserve_ok";
    }
}
