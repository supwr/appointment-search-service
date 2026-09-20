package com.appointmentsearch.api.infrastructure.messaging.consumer;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Profile("worker")
public class AppointmentScheduledConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentScheduledConsumer.class);

    private final ObjectMapper objectMapper;
    private final CreateAppointmentUseCase ingestionUseCase;
    private final com.appointmentsearch.api.application.gateway.AppointmentGateway appointmentGateway;

    public AppointmentScheduledConsumer(
        final ObjectMapper objectMapper,
        final CreateAppointmentUseCase ingestionUseCase,
        final com.appointmentsearch.api.application.gateway.AppointmentGateway appointmentGateway
    ) {
        this.objectMapper = objectMapper;
        this.ingestionUseCase = ingestionUseCase;
        this.appointmentGateway = appointmentGateway;
    }

    @RetryableTopic(
        attempts = "${app.kafka.retry.attempts}",
        retryTopicSuffix = "${app.kafka.retry.retry-topic-suffix}",
        dltTopicSuffix = "${app.kafka.retry.dlq-topic-suffix}",
        sameIntervalTopicReuseStrategy = SameIntervalTopicReuseStrategy.SINGLE_TOPIC
    )
    @KafkaListener(topics = "${app.kafka.topics.appointment-scheduled}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(final ConsumerRecord<String, String> record) throws IOException {
        final AppointmentScheduledEvent event = objectMapper.readValue(record.value(), AppointmentScheduledEvent.class);
        final AppointmentScheduledEvent normalized = normalizeEvent(record.headers(), event);
        final String idempotencyKey = resolveIdempotencyKey(normalized);
        if (appointmentGateway.isProcessed(idempotencyKey)) {
            logger.info("Idempotency key {} already processed; acking", idempotencyKey);
            return;
        }
        ingestionUseCase.execute(normalized);
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
            event.occurredAt(),
            event.fullname(),
            event.email()
        );
    }

    private String headerAsString(final Headers headers, final String key) {
        final Header header = headers.lastHeader(key);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
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
