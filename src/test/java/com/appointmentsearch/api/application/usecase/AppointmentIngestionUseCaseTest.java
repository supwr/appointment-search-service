package com.appointmentsearch.api.application.usecase;

import com.appointmentsearch.api.application.dto.event.AppointmentEventType;
import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.messaging.mapper.AppointmentMessageMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

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
        final AppointmentScheduledEvent event = buildEvent(AppointmentEventType.SCHEDULED);
        final ScheduledAppointment domain = buildDomain(event);
        when(mapper.toDomain(event, "idempotency-1")).thenReturn(domain);

        useCase.execute(event);

        verify(gateway).save(domain);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldUpdateAppointmentWhenExecuted() {
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        final AppointmentMessageMapper mapper = mock(AppointmentMessageMapper.class);
        final CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(gateway, mapper);
        final AppointmentScheduledEvent event = buildEvent(AppointmentEventType.UPDATED);
        final ScheduledAppointment domain = buildDomain(event);
        when(mapper.toDomain(event, "idempotency-1")).thenReturn(domain);

        useCase.execute(event);

        verify(gateway).save(domain);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void shouldDeleteAppointmentWhenExecuted() {
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        final AppointmentMessageMapper mapper = mock(AppointmentMessageMapper.class);
        final CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(gateway, mapper);
        final AppointmentScheduledEvent event = buildEvent(AppointmentEventType.DELETED);

        useCase.execute(event);

        verify(gateway).deleteByAppointmentId(event.appointmentId());
        verifyNoMoreInteractions(gateway, mapper);
    }

    private AppointmentScheduledEvent buildEvent(
        final AppointmentEventType type
    ) {
        return new AppointmentScheduledEvent(
            type,
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            OffsetDateTime.parse("2026-09-07T10:15:30-03:00"),
            "event-1",
            "idempotency-1",
            "Dr. John Doe",
            "john.doe@example.com"
        );
    }

    private ScheduledAppointment buildDomain(final AppointmentScheduledEvent event) {
        return new ScheduledAppointment(
            event.appointmentId(),
            event.patientId(),
            event.doctorId(),
            event.appointmentDateTime(),
            event.eventId(),
            event.idempotencyKey(),
            Instant.now(),
            Instant.now(),
            event.fullname(),
            event.email()
        );
    }
}
