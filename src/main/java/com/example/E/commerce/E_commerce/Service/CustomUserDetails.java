package com.example.E.commerce.E_commerce.Service;

import com.example.E.commerce.E_commerce.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails
{
    public CustomUserDetails(User user) {
        this.user = user;
    }

    private final User user;

    public Long getId()
    {
        return user.getId();
    }
    @Override
    public String getUsername ()
    {
       return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword()
    {
        return user.getPassword_hash();
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
////        return List.of(new SimpleGrantedAuthority(user.getRole()));
//    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}
