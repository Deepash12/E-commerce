package com.example.E.commerce.E_commerce.Repository;

import com.example.E.commerce.E_commerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>
{
//    boolean existsByRole(String roleAdmin);

    Optional<User> findByUsername(String Username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
