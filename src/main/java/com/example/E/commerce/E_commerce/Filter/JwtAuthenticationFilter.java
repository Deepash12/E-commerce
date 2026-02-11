package com.example.E.commerce.E_commerce.Filter;

import com.example.E.commerce.E_commerce.Service.tokenBlackListService;
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
    private final tokenBlackListService tokenBlackListService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, tokenBlackListService tokenBlackListService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (tokenBlackListService.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                Claims claims = jwtUtil.ValidateTokens(token);
                String username = claims.getSubject();
                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    String role = claims.get("role", String.class);

                    if (role == null) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    var authorities = List.of(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    username, null, authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                e.printStackTrace(); // 🔥 debug
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
