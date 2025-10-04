package com.acme.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApiKeyFilter extends OncePerRequestFilter {

  private final String expectedApiKey;
  private final AntPathMatcher matcher = new AntPathMatcher();

  // Rutas públicas que NO requieren API key
  private static final String[] PUBLIC_PATHS = new String[] {
      "/v3/api-docs/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/actuator/health"
  };

  public ApiKeyFilter(String expectedApiKey) {
    this.expectedApiKey = expectedApiKey;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    // Siempre dejar pasar preflight CORS
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    // Whitelist explícito: si la ruta es pública, continuar sin validar
    String path = request.getRequestURI();
    for (String p : PUBLIC_PATHS) {
      if (matcher.match(p, path)) {
        filterChain.doFilter(request, response);
        return;
      }
    }

    // Validar API key en el resto
    String apiKey = request.getHeader("X-API-KEY");
    if (apiKey == null || !apiKey.equals(expectedApiKey)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/vnd.api+json");
      String body = """
        {"errors":[{"status":"401","title":"Unauthorized","detail":"Missing or invalid API key"}]}
        """;
      byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
      response.setContentLength(bytes.length);
      response.getOutputStream().write(bytes);
      return;
    }

    filterChain.doFilter(request, response);
  }
}