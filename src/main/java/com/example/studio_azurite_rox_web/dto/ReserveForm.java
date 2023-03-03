package com.example.studio_azurite_rox_web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReserveForm implements Serializable {
    private Integer id;

    private Integer memberId;

    private String name;

    private String email;

    @NotBlank
    @Size(max=16)
    private String studioType;

    @NotBlank
    private String startDatetime;

    @NotBlank
    private String endDatetime;

    private Boolean engineer;

    private Boolean mastering;

    @Size(max=1024)
    private String note;
}
