package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.*;
import com.example.E.commerce.E_commerce.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class Auth
{
    private final AuthService authService;

    public Auth(AuthService authService) {
        this.authService = authService;
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> Login(@RequestBody AuthDTO request)
//    {
//        try {
//
//
//            Authentication authentication = authenticationManager.authenticate
//                    (
//                            new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
//                    );
//
//            CustomUserDetails userDetails =
//                    (CustomUserDetails) authentication.getPrincipal();
//
//            Long userId = userDetails.getId();
//
//            List<String> roles = userDetails.getAuthorities()
//                    .stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .toList();
//
//            String accessToken = jwtUtil.generateAccessToken
//                    (
//                            userId, userDetails.getUsername(), roles
//                    );
//            String refreshToken = jwtUtil.generateRefreshToken
//                    (
//                            userDetails.getUsername()
//                    );
//            return ResponseEntity.ok( new AuthResponse(accessToken, refreshToken));
//        }
//        catch (BadCredentialsException ex) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Invalid username or password");
//        }
//    }

    @PostMapping("/register")
        public ResponseEntity<String> Register(@RequestBody RegisterRequestDTO registerRequestDTO)
    {

        return ResponseEntity.ok(authService.registerUser(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request)
    {

        return ResponseEntity.ok(authService.loginUser(request));
    }

}
