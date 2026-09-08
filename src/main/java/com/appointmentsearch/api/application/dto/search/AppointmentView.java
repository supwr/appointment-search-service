package com.appointmentsearch.api.application.dto.search;

import com.appointmentsearch.api.domain.model.AppointmentStatus;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AppointmentView(
    UUID appointmentId,
    UUID patientId,
    UUID doctorId,
    OffsetDateTime appointmentDateTime,
    AppointmentStatus status,
    String sourceEventId,
    String idempotencyKey,
    Instant createdAt,
    Instant updatedAt
) {
}
