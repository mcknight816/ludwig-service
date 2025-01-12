package com.bluntsoftware.ludwig.tenant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TenantJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return getRoles(jwt).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public List<String> getRoles(Jwt jwt){
/*
        Optional<TenantUser> tenantUser =  TenantUserService.getUser(jwt.getTokenValue());
        if(tenantUser.isPresent()){
            return tenantUser.get().getRoles();
        }
        throw new RuntimeException("Tenant User not Found");
*/
        ArrayList<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        roles.add("ROLE_SAASY_ADMIN");
        roles.add("ROLE_SAASY_USER");
        return roles;
    }
}

