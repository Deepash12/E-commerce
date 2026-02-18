package com.example.E.commerce.E_commerce.DTO.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddAddressRequestDTO {

    @NotBlank(message = "Full Name is Required")
    private String fullName;
    @NotBlank(message = "Mobile Number is Required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;
    private String addressLine2;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    private String postalCode;
    @NotBlank(message = "country is required")
    private String country;
    private Boolean isDefault;
    @NotBlank(message = "landmark is required")
    private String landmark;
}
