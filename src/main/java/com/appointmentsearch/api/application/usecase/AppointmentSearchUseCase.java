package com.appointmentsearch.api.application.usecase;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.application.gateway.AppointmentProjectionGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentSearchUseCase {

    private final AppointmentProjectionGateway projectionGateway;

    public AppointmentSearchUseCase(final AppointmentProjectionGateway projectionGateway) {
        this.projectionGateway = projectionGateway;
    }

    public AppointmentPage execute(final UUID patientId, final AppointmentSearchFilter filter) {
        return projectionGateway.searchByPatient(patientId, filter);
    }
}
