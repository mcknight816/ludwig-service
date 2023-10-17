package com.bluntsoftware.ludwig.conduit.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {


    private SecurityUtils(ObjectMapper mapper) {

    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();
        Object principal = auth.getPrincipal();
        if(principal instanceof UserDetails){
            UserDetails springSecurityUser = (UserDetails) auth.getPrincipal();
            return springSecurityUser.getUsername();
        }else if(principal instanceof OAuth2User){
            OAuth2User springSecurityUser = (OAuth2User) auth.getPrincipal();
            return springSecurityUser.getName();
        }else if(principal instanceof Jwt){
            Jwt jwt = (Jwt)principal;
            Map claims = jwt.getClaims();
            String username = "not found";
            if(claims.containsKey("user_name")){
                username = claims.get("user_name").toString();
            }else if(claims.containsKey("email")){
                username = claims.get("email").toString();
            }else if(claims.containsKey("user")){
                username = claims.get("user").toString();
            }else if(claims.containsKey("login")){
                username = claims.get("login").toString();
            }
            return username;
        }
        return (String)principal;
    }
    public static ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(javaTimeModule);
    }

    public static Map getPrincipal(){
        Authentication auth = getAuthentication();
        if(auth != null){
            ObjectMapper oMapper = objectMapper();
            Object principal = auth.getPrincipal();
            if(principal != null && !(principal instanceof String)){
                if(principal instanceof Jwt){
                    Jwt jwt = (Jwt)principal;
                    principal = jwt.getClaims();
                }
                return oMapper.convertValue(principal, Map.class);
            }
        }
        return null;
    }
    public static String get(Map map,String key,String def){
        if(map != null){
            Object val = map.get(key);
            if(val != null){
                return val.toString();
            }
        }
        return def;
    }
    public static Map<String,Object> getUserInfo(){
        Map<String,Object> userInfo = new HashMap<>();
        Map<String,Object> principal = getPrincipal();
        if(principal != null){
            String login = SecurityUtils.getCurrentLogin();
            userInfo.put("first_name",get(principal,"givenName","anonymous"));
            userInfo.put("last_name",get(principal,"familyName","anonymous"));
            userInfo.put("email",get(principal,"email","anonymous"));
            userInfo.put("company",get(principal,"company","anonymous"));
            userInfo.put("tenant_id",get(principal,"email","anonymous"));
            userInfo.put("friends",get(principal,"company","anonymous"));
            userInfo.put("picUrl",get(principal,"picture",""));
            userInfo.put("login",login);
            StringBuilder roles = new StringBuilder();
            for(GrantedAuthority authority:SecurityUtils.getAuthorities()){
                roles.append(authority.getAuthority()).append(" ");
            }
            userInfo.put("role", roles.toString());
        }

         return userInfo;
    }
    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        final Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }

        return true;
    }
    public static boolean isAuthorized(String role){
        return getRoles().contains(role);
    }

    public static boolean isAdmin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        final Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.LUDWIG_ADMIN)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public static Collection<? extends GrantedAuthority>  getAuthorities(){
        return getAuthentication() != null && getAuthentication().getAuthorities() != null ? getAuthentication().getAuthorities() : new ArrayList<>();
    }

    public static Collection<String> getRoles() {
        List<String> ret = new ArrayList<>();
        for(GrantedAuthority authority:SecurityUtils.getAuthorities()){
            ret.add(authority.getAuthority());
        }
        return ret;
    }
}
