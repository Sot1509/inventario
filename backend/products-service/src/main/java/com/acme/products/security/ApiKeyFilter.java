package com.acme.products.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  @Value("${app.api-key:secret123}")
  private String apiKey;

  private static final Set<String> WHITELIST_PREFIXES = Set.of(
      "/swagger-ui.html", "/swagger-ui/",
      "/v3/api-docs",     // incluye /v3/api-docs y subrutas
      "/actuator/health"
  );

  private boolean isWhitelisted(String uri) {
    return WHITELIST_PREFIXES.stream().anyMatch(uri::startsWith);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String uri = req.getRequestURI();
    if (isWhitelisted(uri)) {
      chain.doFilter(req, res);
      return;
    }

    String header = req.getHeader("X-API-KEY");
    if (header == null || !header.equals(apiKey)) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      res.setContentType("application/vnd.api+json");
      res.getWriter().write("{\"errors\":[{\"status\":\"401\",\"title\":\"Unauthorized\",\"detail\":\"Missing or invalid API key\"}]}");
      return;
    }

    chain.doFilter(req, res);
  }
}
