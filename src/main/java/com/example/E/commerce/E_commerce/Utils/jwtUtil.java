package com.example.E.commerce.E_commerce.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@UtilityClass
public class jwtUtil
{
    @Value("${Jwt.Secret}")
    private String jwtSecret;
    @Value("${Jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${Jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private Key getSigningKey()
        {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }

    public String generateAccessToken(long userId, String username , List<String> roles)
    {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId",userId)
                .claim("roles",roles)
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
}
