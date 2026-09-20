package com.appointmentsearch.api.infrastructure.messaging.consumer;

import com.appointmentsearch.api.application.dto.event.AppointmentEventType;
import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentScheduledConsumerTest {

    @Test
    void shouldDeserializeEventAndDelegateToUseCase() throws Exception {
        final CreateAppointmentUseCase useCase = mock(CreateAppointmentUseCase.class);
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        when(gateway.isProcessed("idempotency-99")).thenReturn(false);
        final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        final AppointmentScheduledConsumer consumer = new AppointmentScheduledConsumer(objectMapper, useCase, gateway);
        final ConsumerRecord<String, String> record = recordWithHeaders("""
            {
              "type": "UPDATED",
              "appointmentId": "4f264ceb-5f90-4c3f-8afd-345c0c680368",
              "patientId": "fff5d5cb-7ca6-4ec9-b429-4c5b90bc4289",
              "doctorId": "4d1c9baf-da93-4b36-ba45-555beef6badd",
              "appointmentDateTime": "2026-09-07 21:51:22.785 +0000",
              "eventId": "event-99",
              "idempotencyKey": "idempotency-99",
              "fullname": "Dr. John Doe",
              "email": "john.doe@example.com"
            }
            """);

        consumer.listen(record);

        final ArgumentCaptor<AppointmentScheduledEvent> captor = ArgumentCaptor.forClass(AppointmentScheduledEvent.class);
        verify(useCase, times(1)).execute(captor.capture());
        assertEquals(AppointmentEventType.UPDATED, captor.getValue().type());
        assertEquals("event-99", captor.getValue().eventId());
        assertEquals("idempotency-99", captor.getValue().idempotencyKey());
        assertEquals("Dr. John Doe", captor.getValue().fullname());
        assertEquals("john.doe@example.com", captor.getValue().email());
    }

    @Test
    void shouldNotDelegateWhenAlreadyProcessed() throws Exception {
        final CreateAppointmentUseCase useCase = mock(CreateAppointmentUseCase.class);
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        when(gateway.isProcessed("idempotency-99")).thenReturn(true);
        final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        final AppointmentScheduledConsumer consumer = new AppointmentScheduledConsumer(objectMapper, useCase, gateway);
        final ConsumerRecord<String, String> record = recordWithHeaders("""
            {
              "type": "DELETED",
              "appointmentId": "4f264ceb-5f90-4c3f-8afd-345c0c680368",
              "patientId": "fff5d5cb-7ca6-4ec9-b429-4c5b90bc4289",
              "doctorId": "4d1c9baf-da93-4b36-ba45-555beef6badd",
              "appointmentDateTime": "2026-09-07 21:51:22.785 +0000",
              "eventId": "event-99",
              "idempotencyKey": "idempotency-99"
            }
            """);

        consumer.listen(record);

        verify(useCase, never()).execute(org.mockito.ArgumentMatchers.any(AppointmentScheduledEvent.class));
    }

    @Test
    void shouldRejectEventsWithoutRequiredHeaders() {
        final CreateAppointmentUseCase useCase = mock(CreateAppointmentUseCase.class);
        final AppointmentGateway gateway = mock(AppointmentGateway.class);
        final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        final AppointmentScheduledConsumer consumer = new AppointmentScheduledConsumer(objectMapper, useCase, gateway);
        final ConsumerRecord<String, String> record = new ConsumerRecord<>(
            "schedule.appointment.scheduled",
            0,
            0L,
            "key",
            """
            {
              "type": "SCHEDULED",
              "appointmentId": "4f264ceb-5f90-4c3f-8afd-345c0c680368",
              "patientId": "fff5d5cb-7ca6-4ec9-b429-4c5b90bc4289",
              "doctorId": "4d1c9baf-da93-4b36-ba45-555beef6badd",
              "appointmentDateTime": "2026-09-07 21:51:22.785 +0000",
              "eventId": "event-99",
              "idempotencyKey": "idempotency-99"
            }
            """
        );

        assertThrows(IllegalArgumentException.class, () -> consumer.listen(record));
    }

    private ConsumerRecord<String, String> recordWithHeaders(final String payload) {
        final ConsumerRecord<String, String> record = new ConsumerRecord<>(
            "schedule.appointment.scheduled",
            0,
            0L,
            "key",
            payload
        );
        record.headers().add(new RecordHeader("X-Idempotency-Key", "idempotency-99".getBytes(UTF_8)));
        record.headers().add(new RecordHeader("X-Source-Service", "scheduling-service".getBytes(UTF_8)));
        return record;
    }
}
