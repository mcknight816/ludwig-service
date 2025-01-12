package com.bluntsoftware.saasy.utils;

import com.bluntsoftware.saasy.domain.App;
import com.bluntsoftware.saasy.repository.AppRepo;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import java.util.Map;

public class AppAwareJwtDecoder implements JwtDecoder {
    private final AppRepo appRepo;
    private final NimbusJwtDecoder defaultJwtDecoder;

    public AppAwareJwtDecoder(AppRepo appRepo, String defaultJwkSetUri) {
        this.appRepo = appRepo;
        this.defaultJwtDecoder = NimbusJwtDecoder.withJwkSetUri(defaultJwkSetUri).build();
    }

    static class SaasyJwt extends Jwt{
        private final String appId;
        public String getAppId(){
            return this.appId;
        }
        public SaasyJwt(Jwt jwt,String appId) {
            super(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
            this.appId = appId;
        }
    }
    @SneakyThrows
    @Override
    public Jwt decode(String token) throws JwtException {
        App app = null;
        JWT jwt = JWTParser.parse(token);
        Map<String, Object> claims = jwt.getJWTClaimsSet().getClaims();
        if(claims.containsKey("iss")){
            String issuer = claims.get("iss").toString();
             app = appRepo.findAppByJwkSetUriStartsWith(issuer).block();
        }
        if(app == null){
            String APP_ID_KEY = "SASSYAPPID";
            String[] appToken = token.split(APP_ID_KEY);
            token = appToken[0];
            String appId = appToken.length > 1?appToken[1]:null;
            if(appId == null && claims.containsKey("appId")){
               appId = claims.get("appId").toString();
            }
            if(appId != null){
                app = appRepo.findById(appId).block();
            }
        }
        return new SaasyJwt(selectDecoder(app).decode(token), app != null && app.getId() != null ? app.getId(): "");
    }

    private JwtDecoder selectDecoder(App app )   {
        if(app != null && app.getJwkSetUri() != null && !app.getJwkSetUri().isEmpty()){
            return NimbusJwtDecoder.withJwkSetUri(app.getJwkSetUri()).build();
        }
        return defaultJwtDecoder;
    }

}
