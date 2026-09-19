package com.appointmentsearch.api.application.usecase;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import com.appointmentsearch.api.domain.model.AppointmentStatus;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import org.junit.jupiter.api.Test;
import com.appointmentsearch.api.infrastructure.messaging.mapper.AppointmentMessageMapper;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AppointmentIngestionUseCaseTest {

    @Test
    void shouldSaveAppointmentWhenExecuted() {
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        final AppointmentMessageMapper mapper = mock(AppointmentMessageMapper.class);
        final CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(gateway, mapper);
        final AppointmentScheduledEvent event = new AppointmentScheduledEvent(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            OffsetDateTime.parse("2026-09-07T10:15:30-03:00"),
            AppointmentStatus.SCHEDULED,
            "event-1",
            "idempotency-1",
            null
        );

        final ScheduledAppointment domain = new ScheduledAppointment(
            event.appointmentId(),
            event.patientId(),
            event.doctorId(),
            event.appointmentDateTime(),
            event.status(),
            event.eventId(),
            event.idempotencyKey(),
            Instant.now(),
            Instant.now()
        );
        when(mapper.toDomain(event, "idempotency-1")).thenReturn(domain);

        useCase.execute(event);

        verify(gateway).save(any(ScheduledAppointment.class));
        verifyNoMoreInteractions(gateway);
    }
}
