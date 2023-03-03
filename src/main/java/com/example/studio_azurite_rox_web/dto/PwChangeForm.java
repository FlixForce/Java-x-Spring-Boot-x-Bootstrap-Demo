package com.example.studio_azurite_rox_web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class PwChangeForm implements Serializable {
    private Integer id;

    private String name;

    @NotBlank
    @Size(max=16)
    private String currentPassword;

    @NotBlank
    @Size(max=16)
    private String newPassword;
}
