package com.example.E.commerce.E_commerce.Service.User;

import com.example.E.commerce.E_commerce.DTO.Authorization.LoginRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.LoginResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.Role;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.Email.EmailService;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
//@RequiredArgsConstructor
@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  AuthenticationManager authenticationManager;
    private  CustomUserDetailsService customUserDetailsService;
    private  JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public String registerUser(RegisterRequestDTO registerRequestDTO)
    {
        if(userRepository.existsByEmail(registerRequestDTO.getEmail()))
        {
            throw new BadRequestException("Email Already Existed!!!");
        }

        if(userRepository.existsByUsername(registerRequestDTO.getUsername()))
        {
            throw new BadRequestException("Username Already existed!!!");
        }

        User user =  new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword_hash(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhoneNumber());
        user.setRole(Role.USER);
        userRepository.save(user);
        return "Registered Successfully , Please Login";
    }



    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
    {
        Authentication authentication = authenticationManager.authenticate
                (
                        new UsernamePasswordAuthenticationToken
                                (
                                        loginRequestDTO.getEmail(),loginRequestDTO.getPassword()
                                )
                );
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequestDTO.getEmail());

        String token = jwtUtil.generateAccessToken(userDetails);
        return new LoginResponseDTO(token,userDetails.getUsername());
    }

    @Transactional
    public String forgetPassword(String email)
    {
        Optional<User>optionalUser = userRepository.findByEmail(email);
        System.out.println(optionalUser);
        if(optionalUser.isEmpty())
        {
            return ("If email exists, reset link has been sent.");
        }
        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        String resetLink = "http://localhost:3000/reset-password?token="+token;
        emailService.sendResetPasswordEmail(user.getEmail(),resetLink);
         return("If email exists,reset link has been sent.");
    }

    @Transactional
    public ResponseEntity<String> resetPassword(String newPassword, String token)
    {
        User user = userRepository.findByResetToken(token).
                orElseThrow(()-> new BadRequestException("Invalid Token!!!"));

        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now()))
        {
            throw new BadRequestException("Token Expired!!!");
        }
        user.setPassword_hash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        return  ResponseEntity.ok("Password Reset Successfully , Now you can Login");
    }

    public String registerAdmin(RegisterRequestDTO registerRequestDTO)
    {
        if(userRepository.existsByEmail(registerRequestDTO.getEmail()))
        {
            throw new BadRequestException("Email Already Existed!!!");
        }
        if(userRepository.existsByUsername(registerRequestDTO.getUsername()))
        {
            throw new BadRequestException("Username Already Existed!!!");
        }
        User user = new User();
        user.setEmail(registerRequestDTO.getEmail());
        user.setRole(Role.ADMIN);
        user.setPassword_hash(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setPhone(registerRequestDTO.getPhoneNumber());
        user.setUsername(registerRequestDTO.getUsername());
        userRepository.save(user);
        return "Registered Successfully!!!";
    }
}
