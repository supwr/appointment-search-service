package com.appointmentsearch.api.application.dto.event;

import com.appointmentsearch.api.domain.model.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppointmentScheduledEvent(
    UUID appointmentId,
    UUID patientId,
    UUID doctorId,
    @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class)
    OffsetDateTime appointmentDateTime,
    AppointmentStatus status,
    String eventId,
    String idempotencyKey,
    Instant occurredAt
) {
    public AppointmentScheduledEvent {
        status = status == null ? AppointmentStatus.SCHEDULED : status;
    }
}
