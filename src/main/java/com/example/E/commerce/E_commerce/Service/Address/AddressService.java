package com.example.E.commerce.E_commerce.Service.Address;
import com.example.E.commerce.E_commerce.DTO.Address.AddAddressRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Address.AddressResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@Service
public class AddressService {
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    @Transactional
    public String addAddress(AddAddressRequestDTO addAddressRequestDTO,String username)
    {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User Not Found!!!"));

        Long userId = user.getId();
        int addressCount = addressRepository.countByUserId(userId);
        if(addressCount>=5)
        {
            throw new BadRequestException("Address limit reached");
        }
        UserAddresses address = new UserAddresses();
        address.setFullName(addAddressRequestDTO.getFullName());
        address.setPhone(addAddressRequestDTO.getPhone());
        address.setAddressLine1(addAddressRequestDTO.getAddressLine1());
        address.setAddressLine2(addAddressRequestDTO.getAddressLine2());
        address.setCity(addAddressRequestDTO.getCity());
        address.setLandmark(addAddressRequestDTO.getLandmark());
        address.setState(addAddressRequestDTO.getState());
        address.setPostalCode(addAddressRequestDTO.getPostalCode());
        address.setUser(user);
        if(addressCount==0)
        {
            address.setIsDefault(true);
        }
        else if (Boolean.TRUE.equals(addAddressRequestDTO.getIsDefault()))
        {
            addressRepository.cleanDefaultForUser(userId);
            address.setIsDefault(true);
        }
        else
        {
            address.setIsDefault(false);
        }
        addressRepository.save(address);
        return "Address Saved Successfully";
    }

    public Page<AddressResponseDTO> viewAddress(Integer pageNumber, Integer pageSize, String username)
    {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new BadRequestException("User not found with username: "+ username));
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdAt").descending());
        Page<UserAddresses> addresses = addressRepository.findByUser(user,pageable);

        return addresses.map(this::mapToDTO);

    }
    private AddressResponseDTO mapToDTO(UserAddresses userAddresses)
    {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(userAddresses.getId());
        dto.setCity(userAddresses.getCity());
        dto.setPhone(userAddresses.getPhone());
        dto.setAddressLine2(userAddresses.getAddressLine2());
        dto.setLandmark(userAddresses.getLandmark());
        dto.setAddressLine1(userAddresses.getAddressLine1());
        dto.setState(userAddresses.getState());
        dto.setFullName(userAddresses.getFullName());
        dto.setPostalCode(userAddresses.getPostalCode());
        dto.setCountry(userAddresses.getCountry());
        dto.setIsDefault(userAddresses.getIsDefault());
        dto.setCreatedAt(userAddresses.getCreatedAt());
        dto.setUpdatedAt(userAddresses.getUpdatedAt());
        return dto;
    }
}
