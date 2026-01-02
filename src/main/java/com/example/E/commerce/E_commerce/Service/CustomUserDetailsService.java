package com.example.E.commerce.E_commerce.Service;

import com.example.E.commerce.E_commerce.Model.User;
import com.example.E.commerce.E_commerce.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow
                (
                        ()-> new RuntimeException("Invalid Username !!!, User not found")
                );
        return new CustomUserDetails(user);
    }
}
