package com.example.E.commerce.E_commerce.Repository.Address;

import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AddressRepository extends JpaRepository<UserAddresses,Long>
{


    int countByUserId(Long userId);

    void cleanDefaultForUser(Long userId);
}
