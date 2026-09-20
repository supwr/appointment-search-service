package com.appointmentsearch.api.infrastructure.web.filter;

import com.appointmentsearch.api.infrastructure.config.ApiRateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

public class ApiRateLimitFilter extends OncePerRequestFilter {

    private static final String PROBLEM_BASE_URL = "https://api.appointmentsearch.com/problems/";

    private final ApiRateLimitService apiRateLimitService;
    private final ObjectMapper objectMapper;

    public ApiRateLimitFilter(final ApiRateLimitService apiRateLimitService, final ObjectMapper objectMapper) {
        this.apiRateLimitService = apiRateLimitService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            apiRateLimitService.acquirePermit();
            filterChain.doFilter(request, response);
            return;
        } catch (final RequestNotPermitted ex) {
            writeRateLimitedResponse(request, response);
        }
    }

    private void writeRateLimitedResponse(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.TOO_MANY_REQUESTS,
            "Rate limit exceeded"
        );
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setType(URI.create(PROBLEM_BASE_URL + "rate-limit-exceeded"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setHeader("Retry-After", "1");
        objectMapper.writeValue(response.getWriter(), problemDetail);
    }
}
