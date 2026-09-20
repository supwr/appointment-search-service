package com.appointmentsearch.api.infrastructure.security;

import java.util.Set;
import java.util.UUID;

public record KongContextPrincipal(
    UUID userId,
    Set<String> roles
) {
}
