package com.example.E.commerce.E_commerce.Service.Address;
import com.example.E.commerce.E_commerce.DTO.Address.AddAddressRequestDTO;
import com.example.E.commerce.E_commerce.Entity.Address.UserAddresses;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Address.AddressRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import jakarta.transaction.Transactional;
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
}
