package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.Address.AddAddressRequestDTO;
import com.example.E.commerce.E_commerce.Service.Address.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
public class AddressController
{
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/add")
    private ResponseEntity<?> addAddress(@Valid @RequestBody AddAddressRequestDTO addAddressRequestDTO, Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(addressService.addAddress(addAddressRequestDTO,username));
    }
}
