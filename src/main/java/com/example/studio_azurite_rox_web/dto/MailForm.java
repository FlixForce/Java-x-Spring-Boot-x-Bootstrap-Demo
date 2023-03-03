package com.example.studio_azurite_rox_web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
public class MailForm implements Serializable {
    // Name
    @NotBlank
    @Length(max=64)
    private String name;

    // Mail Address
    @NotBlank
    @Email
    @Size(max=32)
    private String email;

    // Content
    @NotBlank
    private String content;
}
