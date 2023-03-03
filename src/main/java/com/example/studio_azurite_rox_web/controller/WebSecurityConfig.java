package com.example.studio_azurite_rox_web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
/*
        httpSecurity.httpBasic().disable();
        httpSecurity.csrf().disable();
*/
        // フォーム認証設定
        httpSecurity.formLogin(login -> login
                // ユーザー名・パスワード送信先URL)
                .loginProcessingUrl("/login")
                // ログイン画面のURL
                .loginPage("/login")
                // ログイン成功後のリダイレクト先URL
                .defaultSuccessUrl("/page?cn=login_ok")
                // ログイン失敗後のリダイレクト先URL
                .failureUrl("/login?error")
                // ログイン画面は、ログイン前でもアクセス可能
                .permitAll()
        // ログアウト設定
        ).logout(logout -> logout
                // ログアウト成功後のリダイレクト先URL
                .logoutSuccessUrl("/login")
        // URLごとの認可設定
        ).authorizeHttpRequests(authz -> authz
                // "/css/**"などはログインなしでもアクセス可能
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/page").permitAll()
                .requestMatchers("/mail_form").permitAll()
                .requestMatchers("/new_registration_form").permitAll()
                .requestMatchers("/error").permitAll()
                // "/member"は、ROLE_MEMBERのみアクセス可能
                .requestMatchers("/member").hasRole("MEMBER")
                // "/admin"は、ROLE_ADMINのみアクセス可能
                .requestMatchers("/admin").hasRole("ADMIN")
                // 他のURLは、ログイン後のみアクセス可能
                .anyRequest().authenticated()
        // セッション設定
        ).sessionManagement(session -> session
                // デフォルト設定
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // ログイン前のセッションを破棄して、新しいセッションを作成
                // ログイン前のオブジェクトは引き継がれない
                .sessionFixation().newSession()
                // 同時ログイン数
                .maximumSessions(1)
                // 先にログインした方が優先される
                .maxSessionsPreventsLogin(true));

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
