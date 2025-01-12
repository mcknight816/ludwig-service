package com.bluntsoftware.ludwig.tenant;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tenant = request.getHeader("tenant-id");
        log.info(request.getMethod() + " " + request.getRequestURI() + " Tenant: " + (tenant != null ? tenant : "No Tenant"));
        TenantResolver.setCurrentTenant(tenant);
        filterChain.doFilter(request, response);
    }
}
