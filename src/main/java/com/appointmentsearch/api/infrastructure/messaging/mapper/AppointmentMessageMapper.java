package com.appointmentsearch.api.infrastructure.messaging.mapper;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AppointmentMessageMapper {

    public ScheduledAppointment toDomain(final AppointmentScheduledEvent event, final String idempotencyKey) {
        final Instant now = Instant.now();
        return new ScheduledAppointment(
            event.appointmentId(),
            event.patientId(),
            event.doctorId(),
            event.appointmentDateTime(),
            event.status(),
            event.eventId(),
            idempotencyKey,
            now,
            now
        );
    }

}
