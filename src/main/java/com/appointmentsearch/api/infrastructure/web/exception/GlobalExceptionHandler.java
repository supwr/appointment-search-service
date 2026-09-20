package com.appointmentsearch.api.infrastructure.web.exception;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
@Profile("api")
public class GlobalExceptionHandler {

    private static final String PROBLEM_BASE_URL = "https://api.appointmentsearch.com/problems/";

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(final AccessDeniedException ex, final WebRequest request) {
        return buildProblem(HttpStatus.FORBIDDEN, "Forbidden", "forbidden", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final WebRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Request", "invalid-request", "Validation failed", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(final IllegalArgumentException ex, final WebRequest request) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Bad Request", "bad-request", ex.getMessage(), request);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleRequestNotPermitted(final RequestNotPermitted ex, final WebRequest request) {
        return buildProblem(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", "rate-limit-exceeded", "Rate limit exceeded", request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(final Exception ex, final WebRequest request) {
        return buildProblem(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "internal-error",
            "An unexpected error occurred",
            request
        );
    }

    private ProblemDetail buildProblem(
        final HttpStatus status,
        final String title,
        final String typeSuffix,
        final String detail,
        final WebRequest request
    ) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(PROBLEM_BASE_URL + typeSuffix));
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
