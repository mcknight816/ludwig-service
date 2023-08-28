package com.bluntsoftware.ludwig.tenant;

import com.bluntsoftware.ludwig.config.SassyConfig;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TenantUserService {

    public static Optional<TenantUser> getUser(){
        return getUser(getAccessToken());
    }

    public static  List<String> getRoles(){
        return getUser().orElseThrow(()->new RuntimeException("User not Found")).getRoles();
    }

    public static Optional<TenantUser> getUser(String accessToken){
        RestTemplate restTemplate = new RestTemplate();
        String customerAPIUrl = SassyConfig.SASSY_URI + "/api/v1/" + SassyConfig.SASSY_APP_ID + "/me/" + TenantResolver.resolve();
        HttpEntity<Map<String,String>> request = new HttpEntity<>(null,buildHeaders(accessToken));
        ResponseEntity<TenantUser> customerEntity = restTemplate.exchange(customerAPIUrl, HttpMethod.POST, request, TenantUser.class );
        return Optional.ofNullable(customerEntity.getBody());
    }

    private static HttpHeaders buildHeaders(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public static String getAccessToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth instanceof AbstractOAuth2TokenAuthenticationToken) {
            AbstractOAuth2TokenAuthenticationToken tok = (AbstractOAuth2TokenAuthenticationToken)auth ;
            return tok.getToken().getTokenValue();
        }
        return null;
    }

}
