package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.AuthDTO;
import com.example.E.commerce.E_commerce.DTO.AuthResponse;
import com.example.E.commerce.E_commerce.Service.CustomUserDetails;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Auth(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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
































//    public Auth(AuthService authService,AuthenticationManager authenticationManager) {
//        this.authService = authService;
//        this.authenticationManager = authenticationManager;
//    }
//    private  AuthService authService;
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("/login")
//    private ResponseEntity<String> Login(@RequestBody AuthDTO authDTO)
//    {
//        Authentication authentication =  authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authDTO.getUserName(),authDTO.getPassword())
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return ResponseEntity.ok("User Logged in Successfully");
//    }
//
//    @PostMapping("/register")
//
//    private String registerUser()
//    {
//        return authService.registerUser();
//    }
//





}
