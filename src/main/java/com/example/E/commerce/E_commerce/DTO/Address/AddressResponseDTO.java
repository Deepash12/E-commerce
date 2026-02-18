package com.example.E.commerce.E_commerce.DTO.Address;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressResponseDTO
{
    private Long id;
    private String fullName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
