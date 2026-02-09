package com.example.E.commerce.E_commerce.Entity;

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

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private String role;

    private String phone;
    private String avatar_url;
    private TinyIntAsSmallIntJdbcType email_verified;
    private String accountStatus;
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    private Date lastLoggedIn;


}
