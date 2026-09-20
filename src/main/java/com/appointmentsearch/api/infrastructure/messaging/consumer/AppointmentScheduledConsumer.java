package com.appointmentsearch.api.infrastructure.messaging.consumer;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
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
    private final AppointmentGateway appointmentGateway;

    public AppointmentScheduledConsumer(
        final ObjectMapper objectMapper,
        final CreateAppointmentUseCase ingestionUseCase,
        final AppointmentGateway appointmentGateway
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
        final String headerIdempotencyKey = requireHeader(headers, "X-Idempotency-Key");
        requireHeader(headers, "X-Source-Service");
        return new AppointmentScheduledEvent(
            event.type(),
            event.appointmentId(),
            event.patientId(),
            event.doctorId(),
            event.appointmentDateTime(),
            event.eventId(),
            headerIdempotencyKey,
            event.fullname(),
            event.email()
        );
    }

    private String requireHeader(final Headers headers, final String key) {
        final Header header = headers.lastHeader(key);
        if (header == null || header.value() == null) {
            throw new IllegalArgumentException("Appointment event must include header " + key);
        }
        final String value = new String(header.value(), StandardCharsets.UTF_8);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Appointment event must include header " + key);
        }
        return value;
    }

    private String resolveIdempotencyKey(final AppointmentScheduledEvent event) {
        return event.idempotencyKey();
    }
}
