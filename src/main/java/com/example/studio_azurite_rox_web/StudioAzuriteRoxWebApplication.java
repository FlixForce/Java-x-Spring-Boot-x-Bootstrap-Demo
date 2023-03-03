package com.example.studio_azurite_rox_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@SpringBootApplication
public class StudioAzuriteRoxWebApplication {
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    public static void main(String[] args) {
        SpringApplication.run(StudioAzuriteRoxWebApplication.class, args);
    }

}
