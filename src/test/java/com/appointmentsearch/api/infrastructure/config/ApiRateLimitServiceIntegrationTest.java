package com.appointmentsearch.api.infrastructure.config;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
class ApiRateLimitServiceIntegrationTest {

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    @Test
    void shouldBlockRequestsAfterConfiguredLimit() {
        for (int i = 0; i < 5; i++) {
            apiRateLimitService.acquirePermit();
        }

        assertThrows(RequestNotPermitted.class, () -> apiRateLimitService.acquirePermit());
    }
}
