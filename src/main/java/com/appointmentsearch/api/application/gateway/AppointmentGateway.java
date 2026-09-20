package com.appointmentsearch.api.application.gateway;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;

import java.util.UUID;

public interface AppointmentGateway {
    void save(ScheduledAppointment appointment);

    void deleteByAppointmentId(UUID appointmentId);

    boolean isProcessed(String idempotencyKey);

    AppointmentPage searchByPatient(UUID patientId, AppointmentSearchFilter filter);
}
