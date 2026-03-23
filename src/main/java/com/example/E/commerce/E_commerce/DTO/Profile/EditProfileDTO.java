package com.example.E.commerce.E_commerce.DTO.Profile;

import com.example.E.commerce.E_commerce.Entity.Authorization.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDTO
{

    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private String phone;
    @NotNull
    private Gender gender;
    @NotNull
    private String email;

    private Boolean removeAvatar;
}
