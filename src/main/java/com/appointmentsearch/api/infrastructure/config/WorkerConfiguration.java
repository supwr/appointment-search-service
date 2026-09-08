package com.appointmentsearch.api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
@Profile("worker")
public class WorkerConfiguration {
}
