package com.appointmentsearch.api.infrastructure.messaging;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.usecase.AppointmentIngestionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;

class AppointmentScheduledConsumerTest {

    @Test
    void shouldDeserializeEventAndDelegateToUseCase() throws Exception {
        final AppointmentIngestionUseCase useCase = mock(AppointmentIngestionUseCase.class);
        final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        final AppointmentScheduledConsumer consumer = new AppointmentScheduledConsumer(objectMapper, useCase);
        final String payload = """
            {
              "appointmentId": "4f264ceb-5f90-4c3f-8afd-345c0c680368",
              "patientId": "fff5d5cb-7ca6-4ec9-b429-4c5b90bc4289",
              "doctorId": "4d1c9baf-da93-4b36-ba45-555beef6badd",
              "appointmentDateTime": "2026-09-07 21:51:22.785 +0000",
              "eventId": "event-99",
              "idempotencyKey": "idempotency-99"
            }
            """;
        final ConsumerRecord<String, String> record = new ConsumerRecord<>(
            "schedule.appointment.scheduled",
            0,
            0L,
            "key",
            payload
        );

        consumer.listen(record);

        final ArgumentCaptor<AppointmentScheduledEvent> captor = ArgumentCaptor.forClass(AppointmentScheduledEvent.class);
        verify(useCase, times(1)).execute(captor.capture());
        assertEquals("event-99", captor.getValue().eventId());
        assertEquals("idempotency-99", captor.getValue().idempotencyKey());
    }
}
