package com.example.studio_azurite_rox_web.Utility;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.io.Serializable;
import java.util.Locale;

@Slf4j
@Component
@Data
public class ErrorMessageGenerator implements Serializable {
    @Autowired
    MessageSource messageSource;

    public ErrorMessageGenerator() {
        //
    }

    public void getErrorMessage(Model model, String errorCode) {
//        log.info("\nError Code = " + errorCode);

        String localizeMessage = messageSource.getMessage("i18nfields.errorCode" + errorCode,
                null, Locale.getDefault());

//        log.info("\nError Message = " + localizeMessage);

        model.addAttribute("exceptionError", true);
        model.addAttribute("errorCodeText", errorCode);
        model.addAttribute("errorMessageText", " | " + localizeMessage);
    }
}
