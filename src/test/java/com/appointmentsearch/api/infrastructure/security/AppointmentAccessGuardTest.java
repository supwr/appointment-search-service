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
        final UUID customerId = UUID.randomUUID();
        final var authentication = authentication("PATIENT", customerId);

        assertTrue(guard.canAccessPatient(customerId, authentication));
        assertFalse(guard.canAccessPatient(UUID.randomUUID(), authentication));
    }

    @Test
    void shouldRequireCustomerId() {
        final UUID customerId = UUID.randomUUID();
        final var authentication = authentication("PATIENT", customerId);

        assertEquals(customerId, guard.requireCustomerId(authentication));
    }

    @Test
    void shouldRejectMissingCustomerId() {
        final var authentication = UsernamePasswordAuthenticationToken.authenticated(
            new KongContextPrincipal(UUID.randomUUID(), null, Set.of("PATIENT")),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_PATIENT"))
        );

        assertThrows(AccessDeniedException.class, () -> guard.requireCustomerId(authentication));
    }

    private UsernamePasswordAuthenticationToken authentication(final String role, final UUID customerId) {
        return UsernamePasswordAuthenticationToken.authenticated(
            new KongContextPrincipal(UUID.randomUUID(), customerId, Set.of(role)),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
