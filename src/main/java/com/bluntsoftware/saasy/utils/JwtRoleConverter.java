package com.bluntsoftware.saasy.utils;


import com.bluntsoftware.saasy.exception.BadRequestException;
import com.bluntsoftware.saasy.repository.TenantRepo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final TenantRepo tenantRepo;

    public JwtRoleConverter(TenantRepo tenantRepo){
        this.tenantRepo = tenantRepo;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        List<String> roles = new ArrayList<>();
        Map<String, Object> claims = jwt.getClaims();

        String issuer = claims.get("iss").toString();
        String realm = issuer.substring(issuer.lastIndexOf("/") + 1);
        String appId = null;

        if(claims.containsKey("appId")){
            appId = claims.get("appId").toString();
        }else if(jwt instanceof AppAwareJwtDecoder.SaasyJwt){
            AppAwareJwtDecoder.SaasyJwt saasyJwt = (AppAwareJwtDecoder.SaasyJwt)jwt;
            appId = saasyJwt.getAppId();
        }

        if(claims.containsKey("realm_access") && realm.equalsIgnoreCase("saasy")) {
            Map<String,Object> realm_access = (Map)claims.get("realm_access");
            if(realm_access.containsKey("roles")){
                roles = (List<String>) realm_access.get("roles");
            }
        }else if(appId != null){
            roles.add("TENANT_USER");
            /*String email = jwt.getClaim("email");
            List<Tenant> tenants = tenantRepo.findAll(QueryHelper.byEmailAndAppId(email,appId)).collectList().block();
            if(tenants != null && tenants.size() > 0){
                roles.add("TENANT");
            }else{
                roles.add("TENANT_USER");
            }*/
        }else{
            throw new BadRequestException("user has no roles assigned");
        }

        return  roles.stream()
                .map(roleName -> "ROLE_" + roleName.replace("/","").toUpperCase())
                .collect(Collectors.toList()).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


}
