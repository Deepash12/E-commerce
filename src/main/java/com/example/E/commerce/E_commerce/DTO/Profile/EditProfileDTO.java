package com.example.E.commerce.E_commerce.DTO.Profile;

import com.example.E.commerce.E_commerce.Entity.Authorization.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDTO
{

    private String firstname;
    private String lastname;
    private String phone;
    private Gender gender;
    private String email;
}
