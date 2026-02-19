package com.example.E.commerce.E_commerce.Repository.Address;

import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<UserAddresses,Long>
{


    int countByUserId(Long userId);

    void cleanDefaultForUser(Long userId);

    Page<UserAddresses> findByUser(User user, Pageable pageable);

    Optional<UserAddresses> findByIdAndUser(Long id, User user);

    boolean deleteByIdAndUser(Long id, User user);

    long countByUser(User user);

    UserAddresses findByIdAndUsername(String username, String addressId);

    UserAddresses findByIdAndUsers(Long addressId, User user);

//    List<UserAddresses> findByUsers(User user);
}
