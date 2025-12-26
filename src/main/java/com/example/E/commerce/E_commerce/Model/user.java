package com.example.E.commerce.E_commerce.Model;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class user
{
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Email getEmail() {
        return Email;
    }

    public void setEmail(Email email) {
        Email = email;
    }

    public BigInteger getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(BigInteger phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getRoles() {
        return Roles;
    }

    public void setRoles(String roles) {
        Roles = roles;
    }

    public Date getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(Date lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    private String Id;
    private String UserName;
    private String Password;
    private Email Email;
    private BigInteger PhoneNumber;
    private String Roles;
    private Date lastLoggedIn;

}
