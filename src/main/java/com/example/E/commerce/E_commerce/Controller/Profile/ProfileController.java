package com.example.E.commerce.E_commerce.Controller.Profile;

import com.example.E.commerce.E_commerce.Controller.Auth;
import com.example.E.commerce.E_commerce.DTO.Profile.EditProfileDTO;
import com.example.E.commerce.E_commerce.DTO.Profile.ProfileResponseDTO;
import com.example.E.commerce.E_commerce.Service.Profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController
{
    private final ProfileService profileService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/view")
    public ResponseEntity<?> viewProfile(Authentication authentication)
    {
        return ResponseEntity.ok(profileService.viewProfile
                (SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/edit")
    public ProfileResponseDTO editProfile(Authentication authentication, @RequestPart EditProfileDTO dto
            , @RequestPart MultipartFile image) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileService.editProfile(username,dto,image);
    }
}
