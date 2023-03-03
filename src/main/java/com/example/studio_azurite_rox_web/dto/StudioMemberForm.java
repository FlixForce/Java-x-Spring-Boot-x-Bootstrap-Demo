package com.example.studio_azurite_rox_web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class StudioMemberForm implements Serializable {
    @Id
    private Integer id;

    @NotBlank
    @Length(max=64)
    private String name;

    @Length(max=128)
    private String furigana;

    @NotBlank
    @Size(max=16)
    private String password;

    @NotBlank
    @Length(max=128)
    private String address;

    @NotBlank
    @Length(max=24)
    @Pattern(regexp = "0\\d{1,4}-\\d{1,4}-\\d{4}")
    private String phone;

    @NotBlank
    @Email
    @Size(max=128)
    private String email;

    @Size(max=128)
    private String artistName;

    @Min(1)
    @Max(10)
    private short memberCount;

    @Size(max=1024)
    private String note;

    private Boolean administrator = false;

    private String registrationDate;
}
