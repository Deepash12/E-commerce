package com.example.E.commerce.E_commerce.Controller;

import com.example.E.commerce.E_commerce.DTO.AuthDTO;
import com.example.E.commerce.E_commerce.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Auth
{
    public Auth(AuthService authService,AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }
    private  AuthService authService;
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    private ResponseEntity<String> Login(@RequestBody AuthDTO authDTO)
    {
        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUserName(),authDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("User Logged in Successfully");
    }

    @PostMapping("/register")

    private String registerUser()
    {
        return authService.registerUser();
    }




}
