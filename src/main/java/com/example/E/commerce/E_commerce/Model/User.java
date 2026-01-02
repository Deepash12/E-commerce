package com.example.E.commerce.E_commerce.Model;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    @Email
    private String email;

    private BigInteger phoneNumber;

    private String role;

    private Date lastLoggedIn;
}
