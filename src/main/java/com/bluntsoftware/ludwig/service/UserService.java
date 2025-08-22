package com.bluntsoftware.ludwig.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    public Map<String, Object> getAuthenticatedUserDetails() {
        // Retrieve the Authentication object from the SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Jwt) {
            // Cast the principal to a Jwt to extract claims
            Jwt jwt = (Jwt) auth.getPrincipal();

            // Extract and return details (e.g., claims in the JWT)
            return jwt.getClaims(); // Returns all claims as a Map<String, Object>
        }

        // If no JWT is present, return null or throw an appropriate exception
        return null;
    }

}
