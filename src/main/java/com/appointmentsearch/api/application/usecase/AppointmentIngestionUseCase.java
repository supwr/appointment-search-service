package com.appointmentsearch.api.application.usecase;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentProjectionGateway;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AppointmentIngestionUseCase {

    private final AppointmentProjectionGateway projectionGateway;

    public AppointmentIngestionUseCase(final AppointmentProjectionGateway projectionGateway) {
        this.projectionGateway = projectionGateway;
    }

    public void execute(final AppointmentScheduledEvent event) {
        final String idempotencyKey = resolveIdempotencyKey(event);
        if (projectionGateway.isProcessed(idempotencyKey)) {
            return;
        }

        projectionGateway.save(toDomain(event, idempotencyKey));
        projectionGateway.markProcessed(idempotencyKey, event.eventId());
    }

    private ScheduledAppointment toDomain(final AppointmentScheduledEvent event, final String idempotencyKey) {
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

    private String resolveIdempotencyKey(final AppointmentScheduledEvent event) {
        if (event.idempotencyKey() != null && !event.idempotencyKey().isBlank()) {
            return event.idempotencyKey();
        }
        if (event.eventId() != null && !event.eventId().isBlank()) {
            return event.eventId();
        }
        throw new IllegalArgumentException("Appointment scheduled event must include idempotencyKey or eventId");
    }
}
