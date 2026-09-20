package com.appointmentsearch.api.application.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppointmentScheduledEvent(
    AppointmentEventType type,
    UUID appointmentId,
    UUID patientId,
    UUID doctorId,
    @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class)
    OffsetDateTime appointmentDateTime,
    String eventId,
    String idempotencyKey,
    String fullname,
    String email
) {
    public AppointmentScheduledEvent {
        type = Objects.requireNonNull(type, "type must not be null");
    }
}
