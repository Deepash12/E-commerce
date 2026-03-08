package com.example.E.commerce.E_commerce.Service.Profile;

import com.example.E.commerce.E_commerce.DTO.Profile.EditProfileDTO;
import com.example.E.commerce.E_commerce.DTO.Profile.ProfileResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.File.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ProfileService
{
    private final UserRepository userRepository;
    private final FileService fileService;

    private ProfileResponseDTO mapTODto(User user)
    {
        ProfileResponseDTO response = new ProfileResponseDTO();
        response.setAvatarUrl(user.getAvatar_url());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirst_name());
        response.setLastName(user.getLast_name());
        response.setPhone(user.getPhone());
        response.setGender(user.getGender());
        return response;
    }
    public ProfileResponseDTO viewProfile(String name)
    {
        User user = userRepository.findByUsername(name)
                    .orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        return mapTODto(user);
    }

    public ProfileResponseDTO editProfile(String username, EditProfileDTO dto, MultipartFile image) throws IOException {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(()-> new BadRequestException("User Not Found!!!"));
        String avatar = fileService.uploadFile(image);
        existingUser.setUsername(dto.getFirstname()+" "+ dto.getLastname());
        existingUser.setEmail(dto.getEmail());
        existingUser.setGender(dto.getGender());
        existingUser.setPhone(dto.getPhone());
        existingUser.setAvatar_url(avatar);
        existingUser.setFirst_name(dto.getFirstname());
        existingUser.setLast_name(dto.getLastname());
        return mapTODto(userRepository.save(existingUser));
    }
}
