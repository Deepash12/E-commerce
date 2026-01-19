package com.example.E.commerce.E_commerce.Service;

import com.example.E.commerce.E_commerce.DTO.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Model.User;
import com.example.E.commerce.E_commerce.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService
{
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public String registerUser(RegisterRequestDTO registerRequestDTO)
    {
//        boolean adminExists = userRepository.existsByRole("ROLE_ADMIN");

        if(userRepository.existsByUsername(registerRequestDTO.getUsername()))
        {
            throw new RuntimeException("Username Already existed!!!");
        }
        if(userRepository.existsByEmail(registerRequestDTO.getEmail()))
        {
            throw new RuntimeException("Email Already Existed!!!");
        }



        User user =  new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword_hash(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setPhone(registerRequestDTO.getPhoneNumber());
//        if(!adminExists)
//        {
//            user.setRole("ROLE_ADMIN");
//        }
//        else
//        {
//            user.setRole(registerRequestDTO.getRoles());
//        }
        userRepository.save(user);
        return "Registered Successfully , Please Login";
    }
}

//    private Auth auth;
//
//    public AuthService(Auth auth) {
//        this.auth = auth;
//    }
//
//    public String userLogin(AuthDTO authDTO)
//    {
//
//        return "";
//    }