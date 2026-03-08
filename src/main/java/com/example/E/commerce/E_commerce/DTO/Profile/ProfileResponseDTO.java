package com.example.E.commerce.E_commerce.DTO.Profile;

import com.example.E.commerce.E_commerce.Entity.Authorization.Gender;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class ProfileResponseDTO
{
    private String avatarUrl;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String username;
    private Gender gender;
}
