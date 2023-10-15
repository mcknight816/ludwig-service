package com.bluntsoftware.ludwig.security;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.security.auth.Subject;
import java.util.Collection;
@Data
@Builder
public class JwtAuthentication implements Authentication {

    Jwt jwt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Jwt getCredentials() {
        return jwt;
    }

    @Override
    public Jwt getDetails() {
        return jwt;
    }

    @Override
    public Jwt getPrincipal() {
        return jwt;
    }

    @Override
    public boolean isAuthenticated() {
        return jwt != null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "JwtAuthentication";
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
