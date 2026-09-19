package com.appointmentsearch.api.application.usecase.create;

import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.messaging.mapper.AppointmentMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAppointmentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateAppointmentUseCase.class);

    private final AppointmentGateway appointmentGateway;
    private final AppointmentMessageMapper mapper;

    public CreateAppointmentUseCase(final AppointmentGateway appointmentGateway, final AppointmentMessageMapper mapper) {
        this.appointmentGateway = appointmentGateway;
        this.mapper = mapper;
    }

    public void execute(final AppointmentScheduledEvent event) {
        final String idempotencyKey = event.idempotencyKey();
        final ScheduledAppointment domain = mapper.toDomain(event, idempotencyKey);
        appointmentGateway.save(domain);
    }

}
