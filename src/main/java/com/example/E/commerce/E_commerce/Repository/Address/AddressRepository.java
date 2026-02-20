package com.example.E.commerce.E_commerce.Repository.Address;

import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<UserAddresses,Long>
{


    int countByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserAddresses a SET a.isDefault = false WHERE a.user.id = :userId")
    void resetDefaultForUser(@Param("userId") Long userId);

    Page<UserAddresses> findByUser(User user, Pageable pageable);

    Optional<UserAddresses> findByIdAndUser(Long id, User user);

//    boolean deleteByIdAndUser(Long id, User user);

    long countByUser(User user);

//    UserAddresses findByIdAndUsername(String username, String addressId);

//    UserAddresses findByIdAndUsers(Long addressId, User user);

//    List<UserAddresses> findByUsers(User user);
}
