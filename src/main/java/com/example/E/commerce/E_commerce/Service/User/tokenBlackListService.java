package com.example.E.commerce.E_commerce.Service.User;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class tokenBlackListService {
    private final Set<String> blacklistedToken = ConcurrentHashMap.newKeySet();
    public void blacklist(String token)
    {
        blacklistedToken.add(token);
    }
    public boolean isBlacklisted(String token)
    {
        return blacklistedToken.contains(token);
    }
}
