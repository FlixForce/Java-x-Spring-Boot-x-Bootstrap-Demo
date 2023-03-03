package com.example.studio_azurite_rox_web.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
public class ReserveInfo {
    @Id
    @NotNull
    private Integer id;
    @NotNull
    private Integer memberId;
    private String studioType;
    private Timestamp startDatetime;
    private Timestamp endDatetime;
    private Boolean engineer;
    private Boolean mastering;
    private String note;
}
