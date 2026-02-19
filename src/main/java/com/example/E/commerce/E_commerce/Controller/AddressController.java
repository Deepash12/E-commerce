package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.Address.AddAddressRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Service.Address.AddressService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/view")
    private Page<AddressResponseDTO> viewAllAddress
            (@PathVariable Integer PageNumber, @PathVariable Integer PageSize,Authentication authentication)
    {
        String username = authentication.getName();;
        return addressService.viewAddress(PageNumber,PageSize,username);
    }

    @GetMapping("/{id}")
    private AddressResponseDTO selectedAddress(@PathVariable Long id, Authentication authentication)
    {
        String username = authentication.getName();
        return addressService.selectedAddress(id,username);
    }

    @PutMapping("/update/{id}")
    private AddressResponseDTO updateAddress
            (@PathVariable Long id, @RequestBody AddAddressRequestDTO addAddressRequestDTO,Authentication authentication)
    {
        String username = authentication.getName();
        return addressService.updateAddress(id,username,addAddressRequestDTO);
    }
}
