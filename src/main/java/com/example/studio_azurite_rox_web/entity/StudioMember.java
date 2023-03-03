package com.example.studio_azurite_rox_web.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
public class StudioMember {
    @Id
    private Integer id;
    private String name;
    private String furigana;
    private String address;
    private String phone;
    private String email;
    private String artistName;
    private Short memberCount;
    private String note;
    private Timestamp registrationDate;
}
