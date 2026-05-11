package com.example.E.commerce.E_commerce.Repository.User;

import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users,Long>
{
    Optional<Users> findByUsername(String Username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);


    Optional<Users> findByResetToken(String token);
}
