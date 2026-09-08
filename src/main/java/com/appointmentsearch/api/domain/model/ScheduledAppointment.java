package com.appointmentsearch.api.domain.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduledAppointment(
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
    public ScheduledAppointment {
        status = status == null ? AppointmentStatus.SCHEDULED : status;
    }
}
