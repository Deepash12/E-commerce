package com.example.E.commerce.E_commerce.DTO.Profile;

import com.example.E.commerce.E_commerce.Entity.Authorization.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
