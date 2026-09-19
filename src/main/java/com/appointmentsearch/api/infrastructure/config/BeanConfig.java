package com.appointmentsearch.api.infrastructure.config;

import com.appointmentsearch.api.application.usecase.create.CreateAppointmentUseCase;
import com.appointmentsearch.api.application.usecase.search.SearchAppointmentUseCase;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.infrastructure.messaging.mapper.AppointmentMessageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateAppointmentUseCase createAppointmentUseCase(
        final AppointmentGateway appointmentGateway,
        final AppointmentMessageMapper mapper
    ) {
        return new CreateAppointmentUseCase(appointmentGateway, mapper);
    }

    @Bean
    public SearchAppointmentUseCase searchAppointmentUseCase(
        final AppointmentGateway appointmentGateway
    ) {
        return new SearchAppointmentUseCase(appointmentGateway);
    }
}
