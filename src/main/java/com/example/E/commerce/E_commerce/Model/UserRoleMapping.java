package com.example.E.commerce.E_commerce.Model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.sql.Timestamp;
@Data
@Entity
public class UserRoleMapping
{
    private Long user_id;
    private Long role_id;
    private Timestamp assignedAt;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Timestamp assignedAt) {
        this.assignedAt = assignedAt;
    }


}
