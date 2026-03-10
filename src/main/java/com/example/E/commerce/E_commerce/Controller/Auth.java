package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.Authorization.*;
import com.example.E.commerce.E_commerce.Service.User.AuthService;
import com.example.E.commerce.E_commerce.Service.User.TokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class Auth
{
    private final AuthService authService;
    private final TokenBlackListService tokenBlackListService;

    public Auth(AuthService authService, TokenBlackListService tokenBlackListService) {
        this.authService = authService;
        this.tokenBlackListService = tokenBlackListService;
    }

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
    @PostMapping("/logout")
    public ResponseEntity<String> LogoutUser(HttpServletRequest request,@RequestBody RefreshTokenRequestDTO requestDTO)
    {
        String header = request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer"))
        {
            String token = header.substring(7);
            tokenBlackListService.blacklist(token);
            tokenBlackListService.blacklist(requestDTO.getRefreshToken());
            return ResponseEntity.ok("logged out Successfully");
        }
        else
        {
            return ResponseEntity.ok("Failed to logout!!!");
        }
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email)
    {
        return  ResponseEntity.ok(authService.forgetPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDtoRequest resetPasswordDtoRequest)
    {
        System.out.println(resetPasswordDtoRequest.getToken());
        System.out.println(resetPasswordDtoRequest.getNewPassword());
        return ResponseEntity.ok
                (authService.resetPassword
                        (resetPasswordDtoRequest.getNewPassword(),resetPasswordDtoRequest.getToken())
                );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAdmin(@RequestBody @Valid RegisterRequestDTO registerRequestDTO)
    {
        return ResponseEntity.ok(authService.registerAdmin(registerRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO token)
    {
        return authService.refreshToken(token);

    }

}
