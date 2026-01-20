package com.example.E.commerce.E_commerce.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.descriptor.jdbc.TinyIntAsSmallIntJdbcType;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Email
    private String email;

    private String password_hash;
    private String first_name;
    private String last_name;

    private String phone;
    private String avatar_url;
    private TinyIntAsSmallIntJdbcType email_verified;
    private String accountStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Date lastLoggedIn;


}
