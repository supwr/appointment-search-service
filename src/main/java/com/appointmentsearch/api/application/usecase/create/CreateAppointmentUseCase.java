package com.appointmentsearch.api.application.usecase.create;

import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.application.dto.event.AppointmentScheduledEvent;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.messaging.mapper.AppointmentMessageMapper;

public class CreateAppointmentUseCase {

    private final AppointmentGateway appointmentGateway;
    private final AppointmentMessageMapper mapper;

    public CreateAppointmentUseCase(final AppointmentGateway appointmentGateway, final AppointmentMessageMapper mapper) {
        this.appointmentGateway = appointmentGateway;
        this.mapper = mapper;
    }

    public void execute(final AppointmentScheduledEvent event) {
        switch (event.type()) {
            case SCHEDULED, UPDATED -> persist(event);
            case DELETED -> appointmentGateway.deleteByAppointmentId(event.appointmentId());
        }
    }

    private void persist(final AppointmentScheduledEvent event) {
        final String idempotencyKey = event.idempotencyKey();
        final ScheduledAppointment domain = mapper.toDomain(event, idempotencyKey);
        appointmentGateway.save(domain);
    }

}
