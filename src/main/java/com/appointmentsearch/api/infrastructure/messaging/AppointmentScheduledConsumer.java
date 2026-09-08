package com.appointmentsearch.api.infrastructure.messaging;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.usecase.AppointmentIngestionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Profile("worker")
public class AppointmentScheduledConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentScheduledConsumer.class);

    private final ObjectMapper objectMapper;
    private final AppointmentIngestionUseCase ingestionUseCase;

    public AppointmentScheduledConsumer(
        final ObjectMapper objectMapper,
        final AppointmentIngestionUseCase ingestionUseCase
    ) {
        this.objectMapper = objectMapper;
        this.ingestionUseCase = ingestionUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.appointment-scheduled}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(final ConsumerRecord<String, String> record) throws IOException {
        final AppointmentScheduledEvent event = objectMapper.readValue(record.value(), AppointmentScheduledEvent.class);
        ingestionUseCase.execute(normalizeEvent(record.headers(), event));
    }

    private AppointmentScheduledEvent normalizeEvent(
        final Headers headers,
        final AppointmentScheduledEvent event
    ) {
        final String headerEventId = headerAsString(headers, "eventId");
        final String headerIdempotencyKey = headerAsString(headers, "X-Idempotency-Key");
        return new AppointmentScheduledEvent(
            event.appointmentId(),
            event.patientId(),
            event.doctorId(),
            event.appointmentDateTime(),
            event.status(),
            event.eventId() != null ? event.eventId() : headerEventId,
            event.idempotencyKey() != null ? event.idempotencyKey() : headerIdempotencyKey,
            event.occurredAt()
        );
    }

    private String headerAsString(final Headers headers, final String key) {
        final Header header = headers.lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
