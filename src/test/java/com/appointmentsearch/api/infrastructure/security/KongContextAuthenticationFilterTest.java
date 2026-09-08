package com.appointmentsearch.api.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KongContextAuthenticationFilterTest {

    private final KongContextAuthenticationFilter filter = new KongContextAuthenticationFilter();

    @Test
    void shouldBuildAuthenticationFromHeaders() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Roles", "PATIENT");
        request.addHeader("X-User-ID", UUID.randomUUID());
        request.addHeader("X-Customer-ID", UUID.randomUUID());

        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication.isAuthenticated());
        assertEquals("ROLE_PATIENT", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }
}
