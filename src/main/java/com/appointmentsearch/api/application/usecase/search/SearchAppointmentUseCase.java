package com.appointmentsearch.api.application.usecase.search;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;

import java.util.UUID;

public class SearchAppointmentUseCase {

    private final AppointmentGateway appointmentGateway;

    public SearchAppointmentUseCase(final AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    public AppointmentPage execute(final UUID patientId, final AppointmentSearchFilter filter) {
        return appointmentGateway.searchByPatient(patientId, filter);
    }
}
