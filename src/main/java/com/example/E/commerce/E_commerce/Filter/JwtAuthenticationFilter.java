package com.example.E.commerce.E_commerce.Filter;

import com.example.E.commerce.E_commerce.Service.User.TokenBlackListService;
import com.example.E.commerce.E_commerce.Utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TokenBlackListService tokenBlackListService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenBlackListService tokenBlackListService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 1. Agar header nahi hai ya Bearer se start nahi ho raha, toh seedha aage bhej do
        // Isse public requests (like /products/all) block nahi hongi
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // 2. Blacklist check
        if (tokenBlackListService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Claims claims = jwtUtil.validateTokens(token);
            String username = claims.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String role = claims.get("role", String.class);

                // Role null check ko handle karein bina aage ki processing roke
                if (role != null) {
                    var authorities = List.of(new SimpleGrantedAuthority(role));
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // Token invalid hai toh context clear karein aur exception entry point ko handle karne dein
            SecurityContextHolder.clearContext();
            // Yahan response.setStatus(401) karne ki zaroorat nahi hai,
            // filterChain aage jayega aur SecurityConfig decide karega access.
        }

        filterChain.doFilter(request, response);
    }
}
