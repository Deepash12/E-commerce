//package com.example.E.commerce.E_commerce.Service.Profile;
//
//import com.example.E.commerce.E_commerce.DTO.Profile.EditProfileDTO;
//import com.example.E.commerce.E_commerce.DTO.Profile.ProfileResponseDTO;
//import com.example.E.commerce.E_commerce.Entity.Authorization.User;
//import com.example.E.commerce.E_commerce.Exception.BadRequestException;
//import com.example.E.commerce.E_commerce.Filter.JwtAuthenticationFilter;
//import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
//import com.example.E.commerce.E_commerce.Service.File.FileService;
//import com.example.E.commerce.E_commerce.Service.User.CustomUserDetails;
//import com.example.E.commerce.E_commerce.Service.User.CustomUserDetailsService;
//import com.example.E.commerce.E_commerce.Utils.JwtUtil;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@Service
//public class ProfileService
//{
//    private final UserRepository userRepository;
//    private final FileService fileService;
//    private final JwtUtil jwtUtil;
//    private final CustomUserDetailsService customUserDetailsService;
//
//
//    private ProfileResponseDTO mapTODto(User user)
//    {
//        ProfileResponseDTO response = new ProfileResponseDTO();
//        response.setAvatarUrl(user.getAvatar_url());
//        response.setEmail(user.getEmail());
//        response.setUsername(user.getUsername());
//        response.setFirstName(user.getFirst_name());
//        response.setLastName(user.getLast_name());
//        response.setPhone(user.getPhone());
//        response.setGender(user.getGender());
//        return response;
//    }
//    public ProfileResponseDTO viewProfile(String name)
//    {
//        User user = userRepository.findByUsername(name)
//                    .orElseThrow(()-> new BadRequestException("User Not Found!!!"));
//        return mapTODto(user);
//    }
////    public ProfileResponseDTO editProfile(String username,
////                                          @Valid EditProfileDTO dto,
////                                          MultipartFile image) throws IOException {
////
////        User existingUser = userRepository.findByUsername(username)
////                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
////
////
////        existingUser.setEmail(dto.getEmail());
////        existingUser.setGender(dto.getGender());
////        existingUser.setPhone(dto.getPhone());
////
////        if (Boolean.TRUE.equals(dto.getRemoveAvatar())) {
////            existingUser.setAvatar_url(null);
////        } else if (image != null && !image.isEmpty()) {
////            String avatarUrl = fileService.uploadFile(image);
////            existingUser.setAvatar_url(avatarUrl);
////        }
////
////        if (dto.getFirstname() != null) {
////            existingUser.setFirst_name(dto.getFirstname());
////        }
////        if (dto.getLastname() != null) {
////            existingUser.setLast_name(dto.getLastname());
////        }
////
////        if (dto.getFirstname() != null && !dto.getFirstname().trim().isEmpty() &&
////                dto.getLastname() != null && !dto.getLastname().trim().isEmpty()) {
////
////            String newUsername = (dto.getFirstname() + "_" + dto.getLastname());
////            existingUser.setUsername(newUsername);
////
////            String newToken = jwtUtil.generateRefreshToken(newUsername);
////
////
////        }
////
////        return mapTODto(userRepository.save(existingUser));
////    }
//
//
//    public ProfileResponseDTO editProfile(String username,
//                                          @Valid EditProfileDTO dto,
//                                          MultipartFile image) throws IOException {
//
//        User existingUser = userRepository.findByUsername(username)
//                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
//
//        existingUser.setEmail(dto.getEmail());
//        existingUser.setGender(dto.getGender());
//        existingUser.setPhone(dto.getPhone());
//
//        if (Boolean.TRUE.equals(dto.getRemoveAvatar())) {
//            existingUser.setAvatar_url(null);
//        } else if (image != null && !image.isEmpty()) {
//            String avatarUrl = fileService.uploadFile(image);
//            existingUser.setAvatar_url(avatarUrl);
//        }
//
//        if (dto.getFirstname() != null) existingUser.setFirst_name(dto.getFirstname());
//        if (dto.getLastname() != null) existingUser.setLast_name(dto.getLastname());
//
//        String newToken = null;
//
//        if (dto.getFirstname() != null && !dto.getFirstname().trim().isEmpty() &&
//                dto.getLastname() != null && !dto.getLastname().trim().isEmpty()) {
//
//            String newUsername = (dto.getFirstname() + "_" + dto.getLastname()).trim();
//
//            if (!newUsername.equals(existingUser.getUsername())) {
//                if (userRepository.existsByUsername(newUsername)) {
//                    throw new BadRequestException("Username " + newUsername + " is already taken!");
//                }
//
//                existingUser.setUsername(newUsername);
//
//                UserDetails updatedUserDetails = org.springframework.security.core.userdetails.User.builder()
//                        .username(newUsername)
//                        .password(existingUser.getPassword_hash()) // Purana password hash hi rahega
//                        .authorities(existingUser.getRole().name()) // User ka current role
//                        .build();
//
//                newToken = jwtUtil.generateAccessToken(updatedUserDetails);
//            }
//        }
//
//        User savedUser = userRepository.save(existingUser);
//        ProfileResponseDTO responseDTO = mapTODto(savedUser);
//
//        if (newToken != null) {
//            responseDTO.setToken(newToken);
//        }
//
//        return responseDTO;
//    }
//}



package com.example.E.commerce.E_commerce.Service.Profile;

import com.example.E.commerce.E_commerce.DTO.Profile.EditProfileDTO;
import com.example.E.commerce.E_commerce.DTO.Profile.ProfileResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.Users;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Service.File.FileService;
import com.example.E.commerce.E_commerce.Service.User.CustomUserDetailsService;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ProfileService
{
    private final UserRepository userRepository;
    private final FileService fileService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private ProfileResponseDTO mapToDto(Users user)
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
        Users user = userRepository.findByUsername(name)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));
        return mapToDto(user);
    }

    public ProfileResponseDTO editProfile(String username,
                                          @Valid EditProfileDTO dto,
                                          MultipartFile image) throws IOException
    {
        Users existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

        // Fields jo username se related nahi — hamesha update honge
        existingUser.setEmail(dto.getEmail());
        existingUser.setGender(dto.getGender());
        existingUser.setPhone(dto.getPhone());

        // Avatar handling
        if (Boolean.TRUE.equals(dto.getRemoveAvatar())) {
            existingUser.setAvatar_url(null);
        } else if (image != null && !image.isEmpty()) {
            String avatarUrl = fileService.uploadFile(image);
            existingUser.setAvatar_url(avatarUrl);
        }

        // First/Last name update
        if (dto.getFirstname() != null) existingUser.setFirst_name(dto.getFirstname());
        if (dto.getLastname() != null) existingUser.setLast_name(dto.getLastname());

        String newAccessToken = null;
        String newRefreshToken = null;

        // Token sirf tab generate hoga jab firstname AUR lastname dono present hain
        // aur unse bana username current username se ALAG ho
        boolean firstnameProvided = dto.getFirstname() != null && !dto.getFirstname().trim().isEmpty();
        boolean lastnameProvided = dto.getLastname() != null && !dto.getLastname().trim().isEmpty();

        if (firstnameProvided && lastnameProvided)
        {
            String newUsername = (dto.getFirstname().trim() + "_" + dto.getLastname().trim());

            if (!newUsername.equals(existingUser.getUsername()))
            {
                if (userRepository.existsByUsername(newUsername)) {
                    throw new BadRequestException("Username " + newUsername + " is already taken!");
                }

                existingUser.setUsername(newUsername);

                UserDetails updatedUserDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(newUsername)
                        .password(existingUser.getPassword_hash())
                        .authorities(existingUser.getRole().name())
                        .build();

                // Dono tokens naye username se generate karo
                newAccessToken = jwtUtil.generateAccessToken(updatedUserDetails);
                newRefreshToken = jwtUtil.generateRefreshToken(newUsername);
            }
        }

        Users savedUser = userRepository.save(existingUser);
        ProfileResponseDTO responseDTO = mapToDto(savedUser);

        // Tokens sirf tab set honge jab username change hua ho
        if (newAccessToken != null) {
            responseDTO.setToken(newAccessToken);
            responseDTO.setRefreshToken(newRefreshToken);
        }

        return responseDTO;
    }
}
