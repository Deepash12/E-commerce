package com.example.E.commerce.E_commerce.Service.User;

import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword_hash())
                .authorities(user.getRole().getRole_name())
                .build();
    }

}
