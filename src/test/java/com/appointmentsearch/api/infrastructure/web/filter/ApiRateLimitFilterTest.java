package com.appointmentsearch.api.infrastructure.web.filter;

import com.appointmentsearch.api.infrastructure.config.ApiRateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ApiRateLimitFilterTest {

    @Test
    void shouldAllowRequestWhenRateLimitServicePermitsIt() throws Exception {
        final ApiRateLimitService rateLimitService = mock(ApiRateLimitService.class);
        final FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(rateLimitService).acquirePermit();
        final ApiRateLimitFilter filter = new ApiRateLimitFilter(rateLimitService, new ObjectMapper().findAndRegisterModules());

        filter.doFilter(new MockHttpServletRequest("GET", "/actuator/health"), new MockHttpServletResponse(), filterChain);

        verify(rateLimitService).acquirePermit();
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimitIsExceeded() throws Exception {
        final ApiRateLimitService rateLimitService = mock(ApiRateLimitService.class);
        final FilterChain filterChain = mock(FilterChain.class);
        doThrow(RequestNotPermitted.createRequestNotPermitted(RateLimiter.ofDefaults("appointmentLimiter")))
            .when(rateLimitService)
            .acquirePermit();
        final ApiRateLimitFilter filter = new ApiRateLimitFilter(rateLimitService, new ObjectMapper().findAndRegisterModules());
        final MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("GET", "/actuator/health"), response, filterChain);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        assertEquals("application/problem+json", response.getContentType());
        assertTrue(response.getContentAsString().contains("rate-limit-exceeded"));
        verify(rateLimitService).acquirePermit();
        verifyNoInteractions(filterChain);
    }
}
