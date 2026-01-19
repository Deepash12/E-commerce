package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.AuthDTO;
import com.example.E.commerce.E_commerce.DTO.AuthResponse;
import com.example.E.commerce.E_commerce.DTO.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Repository.UserRepository;
import com.example.E.commerce.E_commerce.Service.AuthService;
import com.example.E.commerce.E_commerce.Service.CustomUserDetails;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class Auth
{
    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Auth(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, AuthService authService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody AuthDTO request)
    {
        try {


            Authentication authentication = authenticationManager.authenticate
                    (
                            new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
                    );

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            Long userId = userDetails.getId();

            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtUtil.generateAccessToken
                    (
                            userId, userDetails.getUsername(), roles
                    );
            String refreshToken = jwtUtil.generateRefreshToken
                    (
                            userDetails.getUsername()
                    );
            return ResponseEntity.ok( new AuthResponse(accessToken, refreshToken));
        }
        catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @PostMapping("/register")
        public ResponseEntity<String> Register(@RequestBody RegisterRequestDTO registerRequestDTO)
    {

        return ResponseEntity.ok(authService.registerUser(registerRequestDTO));
    }

//    @GetMapping("/admin-Exists")
//    public Map<String,Boolean> adminExists()
//    {
//        boolean exists = userRepository.existsByRole("ROLE_ADMIN");
//        return Map.of("Admin Exists",exists);
//    }
}
