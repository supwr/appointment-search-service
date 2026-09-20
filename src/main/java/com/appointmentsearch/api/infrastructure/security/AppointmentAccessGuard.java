package com.appointmentsearch.api.infrastructure.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@Profile("api")
public class AppointmentAccessGuard {

    private static final Set<String> STAFF_ROLES = Set.of("DOCTOR", "NURSE", "ADMIN");

    public boolean canAccessPatient(final UUID patientId, final Authentication authentication) {
        if (patientId == null || authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        if (hasAnyStaffRole(authentication)) {
            return true;
        }

        final KongContextPrincipal principal = principal(authentication);
        return hasRole(authentication, "PATIENT") && patientId.equals(principal.userId());
    }

    public UUID requireUserId(final Authentication authentication) {
        final KongContextPrincipal principal = principal(authentication);
        if (principal.userId() == null) {
            throw new AccessDeniedException("Missing X-User-ID header");
        }
        return principal.userId();
    }

    private boolean hasAnyStaffRole(final Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority().replace("ROLE_", ""))
            .anyMatch(STAFF_ROLES::contains);
    }

    private boolean hasRole(final Authentication authentication, final String role) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    private KongContextPrincipal principal(final Authentication authentication) {
        final Object principal = authentication.getPrincipal();
        if (!(principal instanceof KongContextPrincipal kongContextPrincipal)) {
            throw new AccessDeniedException("Invalid authentication principal");
        }
        return kongContextPrincipal;
    }
}
