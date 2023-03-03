package com.example.studio_azurite_rox_web.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudioMemberSearchForm implements Serializable {
    private Integer id;

    private String name;
}
