package com.appointmentsearch.api.infrastructure.config;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("api")
public class ApiRateLimitService {

    @RateLimiter(name = "appointmentLimiter")
    public void acquirePermit() {
    }
}
