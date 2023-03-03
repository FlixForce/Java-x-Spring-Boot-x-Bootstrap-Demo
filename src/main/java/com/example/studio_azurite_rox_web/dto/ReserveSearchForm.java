package com.example.studio_azurite_rox_web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class ReserveSearchForm implements Serializable {
    @Id
    private Integer id;

    private Integer memberId;

    @NotBlank
    @Length(max=64)
    private String name;

    @NotBlank
    private String startDatetime;
}
