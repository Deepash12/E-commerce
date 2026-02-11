package com.example.E.commerce.E_commerce.DTO.Authorization;


public class AuthDTO
{
    public  String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    private static String username;

    public  String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        password = password;
    }

    private static String password;



}
