package com.projeto.unify.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        log.debug("Processing request to '{}', Authorization header: {}", request.getRequestURI(), authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in request to '{}'", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extrairUsername(jwt);
            log.debug("JWT token found for user: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("User details loaded: {}, Authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());

                if (jwtService.isTokenValido(jwt, userDetails)) {
                    // Extract authorities from token
                    String authoritiesStr = jwtService.extrairClaim(jwt, claims -> claims.get("authorities", String.class));
                    List<SimpleGrantedAuthority> authorities = Arrays.stream(authoritiesStr.split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    log.debug("Extracted authorities from token: {}", authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities // Use authorities from token
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication token set in SecurityContext for user: {} with authorities: {}",
                            userEmail, authorities);
                } else {
                    log.debug("Token validation failed for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token", e);
        }

        filterChain.doFilter(request, response);
    }
}