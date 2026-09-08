package com.appointmentsearch.api.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Profile("api")
public class KongContextAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ROLES_HEADER = "X-User-Roles";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CUSTOMER_ID_HEADER = "X-Customer-ID";

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        final String rolesHeader = request.getHeader(USER_ROLES_HEADER);
        final Set<String> roles = parseRoles(rolesHeader);
        final UUID userId = parseUuidHeader(request.getHeader(USER_ID_HEADER));
        final UUID customerId = parseUuidHeader(request.getHeader(CUSTOMER_ID_HEADER));

        final KongContextPrincipal principal = new KongContextPrincipal(userId, customerId, roles);
        final UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
            principal,
            null,
            roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private Set<String> parseRoles(final String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(rolesHeader.split(","))
            .map(String::trim)
            .filter(role -> !role.isBlank())
            .map(role -> role.toUpperCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    private UUID parseUuidHeader(final String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }
        return UUID.fromString(headerValue.trim());
    }
}
