package com.khataapp.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final com.khataapp.repository.KhataRepository repository;

    public JwtAuthenticationFilter(JwtService jwtService, com.khataapp.repository.KhataRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String subject = jwtService.subject(header.substring(7));
                if (subject != null && !subject.isBlank() && repository.findUserById(subject).isPresent()) {
                    String role = repository.findUserById(subject).map(com.khataapp.model.KhataItem::getRole).orElse("USER");
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(subject, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))));
                }
            } catch (ExpiredJwtException ex) {
                SecurityContextHolder.clearContext();
                if (request.getRequestURI().startsWith("/api/v1/auth/")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/problem+json");
                response.getWriter().write(
                        "{\"type\":\"about:blank\",\"title\":\"Unauthorized\",\"status\":401,\"detail\":\"Token has expired\"}");
                return;
            } catch (JwtException | IllegalArgumentException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
