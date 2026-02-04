package com.example.E.commerce.E_commerce.Controller;
import com.example.E.commerce.E_commerce.DTO.*;
import com.example.E.commerce.E_commerce.Service.AuthService;
import com.example.E.commerce.E_commerce.Service.tokenBlackListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class Auth
{
    private final AuthService authService;
    private final tokenBlackListService tokenBlackListService;

    public Auth(AuthService authService, tokenBlackListService tokenBlackListService) {
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
    public ResponseEntity<String> LogoutUser(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer"))
        {
            String token = header.substring(7);
            tokenBlackListService.blacklist(token);
            return ResponseEntity.ok("logged out Successfully");
        }
        else
        {
            return ResponseEntity.ok("Failed to logout!!!");
        }
    }

}
