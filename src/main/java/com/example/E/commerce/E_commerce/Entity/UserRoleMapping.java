package com.example.E.commerce.E_commerce.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleMapping
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapping_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    private Timestamp assignedAt;



}
