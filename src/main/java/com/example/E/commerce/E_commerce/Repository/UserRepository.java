package com.example.E.commerce.E_commerce.Repository;

import com.example.E.commerce.E_commerce.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByUsername(String Username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
