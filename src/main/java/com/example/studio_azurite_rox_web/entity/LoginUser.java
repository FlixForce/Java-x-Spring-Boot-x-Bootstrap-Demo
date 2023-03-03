package com.example.studio_azurite_rox_web.entity;

import java.util.List;

public record LoginUser(
    Integer id,
    String email,
    String name,
    String password,
    List<String> roleList) {
}
