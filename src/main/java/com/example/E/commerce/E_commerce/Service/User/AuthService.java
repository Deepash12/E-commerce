package com.example.E.commerce.E_commerce.Service.User;

import com.example.E.commerce.E_commerce.DTO.Authorization.LoginRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.LoginResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.RefreshTokenRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Authorization.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.Role;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.Email.EmailService;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  AuthenticationManager authenticationManager;
    private  CustomUserDetailsService customUserDetailsService;
    private final TokenBlackListService tokenBlackListService;
    private  JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, TokenBlackListService tokenBlackListService, JwtUtil jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.tokenBlackListService = tokenBlackListService;
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
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        System.out.println(refreshToken);
        return new LoginResponseDTO(token,userDetails.getUsername(),refreshToken);
    }

    @Transactional
    public String forgetPassword(String email)
    {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        if(user==null)
        {
            return ("If email exists, reset link has been sent.");
        }
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
        System.out.println(token);
        System.out.println(newPassword);
        User user = userRepository.findByResetToken(token).
                orElseThrow(()-> new BadRequestException("Invalid Token!!!"));

        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now()))
        {
            throw new BadRequestException("Token Expired!!!");
        }
        user.setPassword_hash(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
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

    public ResponseEntity<?> refreshToken(RefreshTokenRequestDTO token)
    {
        String refreshToken = token.getRefreshToken();
        if(tokenBlackListService.isBlacklisted(refreshToken))
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Claims claims = jwtUtil.validateTokens(refreshToken);
        User user = userRepository.findByUsername(claims.getSubject())
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        if(jwtUtil.isTokenValid(refreshToken,userDetails))
        {
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
