package com.appointmentsearch.api.application.gateway;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;

import java.util.UUID;

public interface AppointmentProjectionGateway {
    void save(ScheduledAppointment appointment);

    boolean isProcessed(String idempotencyKey);

    void markProcessed(String idempotencyKey, String eventId);

    AppointmentPage searchByPatient(UUID patientId, AppointmentSearchFilter filter);
}
