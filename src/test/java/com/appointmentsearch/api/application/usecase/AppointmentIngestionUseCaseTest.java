package com.appointmentsearch.api.application.usecase;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentProjectionGateway;
import com.appointmentsearch.api.domain.model.AppointmentStatus;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class AppointmentIngestionUseCaseTest {

    @Test
    void shouldSaveAppointmentAndMarkEventAsProcessed() {
        final AppointmentProjectionGateway gateway = mock(AppointmentProjectionGateway.class);
        final AppointmentIngestionUseCase useCase = new AppointmentIngestionUseCase(gateway);
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

        useCase.execute(event);

        verify(gateway).isProcessed("idempotency-1");
        verify(gateway).save(any(ScheduledAppointment.class));
        verify(gateway).markProcessed("idempotency-1", "event-1");
        verifyNoMoreInteractions(gateway);
    }
}
