package com.example.E.commerce.E_commerce.Repository;

import com.example.E.commerce.E_commerce.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByUsername(String Username);

    boolean existsByusername(String username);

    boolean existsByEmail(String email);
}
