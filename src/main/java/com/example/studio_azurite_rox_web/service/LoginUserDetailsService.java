package com.example.studio_azurite_rox_web.service;

import com.example.studio_azurite_rox_web.entity.LoginUser;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.repository.LoginUserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginUserDetailsService implements UserDetailsService {
    private final LoginUserRepository loginUserRepository;

    public LoginUserDetailsService(LoginUserRepository loginUserRepository) throws DataAccessException {
        this.loginUserRepository = loginUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<LoginUser> loginUserOptional = loginUserRepository.findByEmail(email);
        return loginUserOptional.map(loginUser -> new LoginUserDetails(loginUser))
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }
}
