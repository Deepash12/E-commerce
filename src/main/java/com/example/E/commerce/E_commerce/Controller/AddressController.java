package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.Address.AddAddressRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.DTO.PaginationResponse;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Service.Address.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController
{
    private final AddressService addressService;

    @PostMapping("/add")
    private ResponseEntity<?> addAddress(@Valid @RequestBody AddAddressRequestDTO addAddressRequestDTO, Authentication authentication)
    {
        String username = authentication.getName();
        return ResponseEntity.ok(addressService.addAddress(addAddressRequestDTO,username));
    }
    @GetMapping("/view")
    private PaginationResponse<AddressResponseDTO> viewAllAddress
            (@RequestParam (defaultValue = "0") Integer PageNumber, @RequestParam(defaultValue = "5") Integer PageSize)
    {
//        String username = authentication.getName();;
        Page<AddressResponseDTO> response =  addressService.viewAddress(PageNumber,PageSize);
        return new PaginationResponse<>(response.getContent(),response);
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

    @DeleteMapping("/delete/address/{id}")
    private ResponseEntity<String> deleteAddress(@PathVariable Long id,Authentication authentication)
    {
        String username= authentication.getName();
        return ResponseEntity.ok(addressService.deleteAddress(id,username));
    }
}
