package com.bluntsoftware.ludwig.security;

import com.bluntsoftware.ludwig.domain.Application;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Configuration
public class JwtFilter extends OncePerRequestFilter {

    private final ApplicationService applicationService;

    public JwtFilter(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String[] pathInfo = httpServletRequest.getServletPath().split("/");
        boolean isApiCall = pathInfo[1] != null && pathInfo[1].equalsIgnoreCase("api");

        if (isApiCall && pathInfo[2] != null && !pathInfo[2].equalsIgnoreCase("swagger")) {
            String appPath = pathInfo[2];
            String flowName = pathInfo[3];
            Application app = applicationService.findByPath(appPath);
            if (app != null && app.getJwkUri() != null && !app.getJwkUri().isEmpty()) {
                log.info("checking token for secured api request... ");
                Flow flow = app.getFlows().stream().filter(f -> f.getName().equalsIgnoreCase(flowName)).findFirst().orElse(null);
                if (flow != null && flow.getLocked()) {
                    String jwkUri = app.getJwkUri();
                    String token = httpServletRequest.getHeader("x-jwt-token");
                    if (token != null && !token.equalsIgnoreCase("")){
                        JwtDecoder decoder = selectDecoder(jwkUri);
                        try {
                            Jwt jwt = decoder.decode(token);
                            if (jwt != null) {
                                SecurityContext securityContext = SecurityContextHolder.getContext();
                                JwtAuthentication jwtAuthentication = JwtAuthentication.builder().jwt(jwt).build();
                                jwtAuthentication.setAuthenticated(true);
                                securityContext.setAuthentication(jwtAuthentication);
                                log.info("token is valid for secured api request.");
                            }
                        } catch(Exception e){
                            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token " + e.getMessage());
                        }
                    } else {
                        httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), " API Key x-jwt-token not found in header");
                    }
                }
            } else if(app == null){
                httpServletResponse.sendError(HttpStatus.NOT_FOUND.value(), "Application with path /api/" + appPath + " not found");
            } else {
                log.info("api request is un secured");
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private JwtDecoder selectDecoder(String jwkUri) {
        return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
    }

}
