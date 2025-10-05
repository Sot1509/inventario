package com.acme.products.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Value("${app.api-key}")
    private String expected;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {
        String key = request.getHeader("X-API-KEY");
        if (key == null || !key.equals(expected)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/vnd.api+json");
        response.getWriter().write("{\"errors\":[{\"status\":\"401\",\"title\":\"Unauthorized\",\"detail\":\"Missing or invalid API key\"}]} ");
        return;
        }
        filterChain.doFilter(request, response);
    }
}