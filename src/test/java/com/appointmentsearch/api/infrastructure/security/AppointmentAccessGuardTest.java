package com.appointmentsearch.api.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentAccessGuardTest {

    private final AppointmentAccessGuard guard = new AppointmentAccessGuard();

    @Test
    void shouldAllowStaffToAccessAnyPatient() {
        final var authentication = authentication("DOCTOR", UUID.randomUUID());

        assertTrue(guard.canAccessPatient(UUID.randomUUID(), authentication));
    }

    @Test
    void shouldAllowPatientToAccessOwnDataOnly() {
        final UUID userId = UUID.randomUUID();
        final var authentication = authentication("PATIENT", userId);

        assertTrue(guard.canAccessPatient(userId, authentication));
        assertFalse(guard.canAccessPatient(UUID.randomUUID(), authentication));
    }

    @Test
    void shouldRequireUserId() {
        final UUID userId = UUID.randomUUID();
        final var authentication = authentication("PATIENT", userId);

        assertEquals(userId, guard.requireUserId(authentication));
    }

    private UsernamePasswordAuthenticationToken authentication(final String role, final UUID userId) {
        return UsernamePasswordAuthenticationToken.authenticated(
            new KongContextPrincipal(userId, Set.of(role)),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
