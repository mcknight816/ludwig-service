package com.bluntsoftware.saasy.service;


import com.bluntsoftware.saasy.domain.Roles;
import com.bluntsoftware.saasy.domain.User;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.utils.JwtRoleConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserInfoService {
    private final TenantRepo tenantRepo;

    public UserInfoService(TenantRepo tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    public User getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal instanceof Jwt) {
            return getUser((Jwt)principal);
        }
        return null;
    }

    public Collection<GrantedAuthority> getRoles(Jwt jwt){
        JwtRoleConverter converter = new JwtRoleConverter(this.tenantRepo);
        return converter.convert(jwt);
    }

    public User getUser(Jwt jwt){
        return User.builder()
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .roles(getRoles(jwt).stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())).build();
    }

    public boolean hasRole(Roles role) {
        return getLoggedInUser().getRoles().contains(role.getRoleName());
    }

    public boolean isAdmin(){
        return hasRole(Roles.ADMIN);
    }

    public boolean isUser(){
        return hasRole(Roles.USER);
    }



}
