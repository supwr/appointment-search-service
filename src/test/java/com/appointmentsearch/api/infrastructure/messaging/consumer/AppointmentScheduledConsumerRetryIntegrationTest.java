package com.appointmentsearch.api.infrastructure.messaging.consumer;

import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
    "spring.main.web-application-type=none",
    "spring.kafka.consumer.auto-offset-reset=earliest",
    "spring.kafka.consumer.group-id=appointment-search-service-test",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@EmbeddedKafka(
    partitions = 1,
    topics = { "scheduling.appointment.scheduled", "scheduling.appointment.scheduled-retry", "scheduling.appointment.scheduled-dlq" },
    bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("worker")
@Import(AppointmentScheduledConsumerRetryIntegrationTest.MockConfig.class)
class AppointmentScheduledConsumerRetryIntegrationTest {

    static final String MAIN_TOPIC = "scheduling.appointment.scheduled";
    static final String RETRY_TOPIC = MAIN_TOPIC + "-retry";
    static final String DLQ_TOPIC = MAIN_TOPIC + "-dlq";
    private static final String PAYLOAD = """
        {
          "appointmentId": "4f264ceb-5f90-4c3f-8afd-345c0c680368",
          "patientId": "fff5d5cb-7ca6-4ec9-b429-4c5b90bc4289",
          "doctorId": "4d1c9baf-da93-4b36-ba45-555beef6badd",
          "appointmentDateTime": "2026-09-07 21:51:22.785 +0000",
          "eventId": "event-99",
          "idempotencyKey": "idempotency-99",
          "fullname": "Dr. John Doe",
          "email": "john.doe@example.com"
        }
        """;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private CreateAppointmentUseCase ingestionUseCase;

    @Autowired
    private AppointmentGateway appointmentGateway;

    @BeforeEach
    void setUp() {
        when(appointmentGateway.isProcessed("idempotency-99")).thenReturn(false);
    }

    @Test
    void shouldRetryMessageUntilItSucceeds() throws Exception {
        doThrow(new IllegalStateException("boom-1"))
            .doThrow(new IllegalStateException("boom-2"))
            .doThrow(new IllegalStateException("boom-3"))
            .doNothing()
            .when(ingestionUseCase).execute(any());

        kafkaTemplate.send(MAIN_TOPIC, PAYLOAD).get(10, TimeUnit.SECONDS);

        verify(ingestionUseCase, timeout(15_000).times(4)).execute(any());
        verify(appointmentGateway, timeout(15_000).atLeast(4)).isProcessed("idempotency-99");
    }

    @Test
    void shouldSendMessageToDlqAfterRetryExhaustion() throws Exception {
        doThrow(new IllegalStateException("boom"))
            .when(ingestionUseCase).execute(any());

        kafkaTemplate.send(MAIN_TOPIC, PAYLOAD).get(10, TimeUnit.SECONDS);

        verify(ingestionUseCase, timeout(15_000).times(4)).execute(any());

        try (Consumer<String, String> consumer = createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, DLQ_TOPIC);
            final var records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
            assertFalse(records.isEmpty());
            final ConsumerRecord<String, String> record = records.iterator().next();
            assertEquals(PAYLOAD, record.value());
        }
    }

    private Consumer<String, String> createConsumer() {
        final Map<String, Object> props = KafkaTestUtils.consumerProps(
            "appointment-search-service-dlq-checker",
            "true",
            embeddedKafkaBroker
        );
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer()).createConsumer();
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        CreateAppointmentUseCase ingestionUseCase() {
            return mock(CreateAppointmentUseCase.class);
        }

        @Bean
        @Primary
        AppointmentGateway appointmentGateway() {
            return mock(AppointmentGateway.class);
        }
    }
}
