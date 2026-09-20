package com.appointmentsearch.api.application.dto.search;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentView(
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
