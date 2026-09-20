package com.appointmentsearch.api.domain.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduledAppointment(
    UUID appointmentId,
    UUID patientId,
    UUID doctorId,
    OffsetDateTime appointmentDateTime,
    String sourceEventId,
    String idempotencyKey,
    Instant createdAt,
    Instant updatedAt,
    String fullname,
    String email
) {
}
