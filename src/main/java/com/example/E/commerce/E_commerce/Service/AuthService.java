package com.example.E.commerce.E_commerce.Service;

import com.example.E.commerce.E_commerce.DTO.Authorization.LoginRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.LoginResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Role;
import com.example.E.commerce.E_commerce.Entity.User;
import com.example.E.commerce.E_commerce.Repository.RoleRepository;
import com.example.E.commerce.E_commerce.Repository.UserRepository;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private CustomUserDetailsService customUserDetailsService;
    private JwtUtil jwtUtil;
    private final RoleRepository role;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       CustomUserDetailsService customUserDetailsService, RoleRepository role)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;

        this.role = role;
    }


    public String registerUser(RegisterRequestDTO registerRequestDTO)
    {

        if(userRepository.existsByUsername(registerRequestDTO.getUsername()))
        {
            throw new RuntimeException("Username Already existed!!!");
        }
        if(userRepository.existsByEmail(registerRequestDTO.getEmail()))
        {
            throw new RuntimeException("Email Already Existed!!!");
        }

        Role role1 = role.findById
                (
                        registerRequestDTO.getRoleId()
                ).orElseThrow(() -> new RuntimeException("Role not found"));
        User user =  new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword_hash(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhoneNumber());
        user.setRole(role1);
        userRepository.save(user);
        return "Registered Successfully , Please Login";
    }



    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
    {
        Authentication authentication = authenticationManager.authenticate
                (
                        new UsernamePasswordAuthenticationToken
                                (
                                        loginRequestDTO.getUsername(),loginRequestDTO.getPassword()
                                )
                );
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequestDTO.getUsername());

        String token = jwtUtil.generateAccessToken(userDetails);
        return new LoginResponseDTO(token,userDetails.getUsername());
    }
}
