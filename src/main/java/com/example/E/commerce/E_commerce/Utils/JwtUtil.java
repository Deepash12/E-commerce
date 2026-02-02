package com.example.E.commerce.E_commerce.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil
{
    @Value("${jwt.secret}")
//    @Value("9f3c7a2d8e41b6f0c2a9d4e57b8f1a6c0e9d3b5a7f4c2e8a1d6b9f0e3c4")
//    @Value("n0w6LY5BtvDCqdTle48abA6dO1p/RMLooa1rmw5jNA=")
    private String jwtSecret;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private Key getSigningKey()
        {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }

    public String generateAccessToken(UserDetails userDetails)
    {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
//                .claim("userId",userId)
//                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username)
    {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenExpiration))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }
    public Claims ValidateTokens(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String extractUsername(String token) {
        return ValidateTokens(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return ValidateTokens(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

//    public String generateAccessToken(UserDetails userDetails) {
//    }
}
