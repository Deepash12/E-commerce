package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.AuthDTO;
import com.example.E.commerce.E_commerce.DTO.AuthResponse;
import com.example.E.commerce.E_commerce.DTO.RegisterRequestDTO;
import com.example.E.commerce.E_commerce.Service.AuthService;
import com.example.E.commerce.E_commerce.Service.CustomUserDetails;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class Auth
{
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Auth(JwtUtil jwtUtil, AuthenticationManager authenticationManager,AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse Login(@RequestBody AuthDTO request)
    {
        Authentication authentication = authenticationManager.authenticate
                (
                        new UsernamePasswordAuthenticationToken(request.getUserName(),request.getPassword())
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
                        userId,userDetails.getUsername(), roles
                );
        String refreshToken = jwtUtil.generateRefreshToken
                (
                        userDetails.getUsername()
                );
        return new AuthResponse(accessToken,refreshToken);
    }

@PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody RegisterRequestDTO registerRequestDTO)
{

    return ResponseEntity.ok(authService.registerUser(registerRequestDTO));
}

}
