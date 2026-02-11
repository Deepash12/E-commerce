package com.example.E.commerce.E_commerce.DTO.Authorization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequestDTO
{
    @NotBlank(message = "Username is Required")
    private String username;
    @NotBlank(message = "Password is Required")
    private String password;
    private Long roleId;
//    private String roles;
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;
    @Email(message = "Invalid Email is Written")
    @NotBlank(message = "Email is Required")
    private String email;

}
