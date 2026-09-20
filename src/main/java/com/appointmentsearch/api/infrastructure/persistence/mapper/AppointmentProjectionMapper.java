package com.appointmentsearch.api.infrastructure.persistence.mapper;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentView;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentDocument;
import org.springframework.data.domain.Page;

import java.util.List;

public final class AppointmentProjectionMapper {

    private AppointmentProjectionMapper() {
    }

    public static AppointmentDocument toDocument(final ScheduledAppointment appointment) {
        return new AppointmentDocument(
            appointment.appointmentId().toString(),
            appointment.appointmentId(),
            appointment.patientId(),
            appointment.doctorId(),
            appointment.appointmentDateTime(),
            appointment.status(),
            appointment.sourceEventId(),
            appointment.idempotencyKey(),
            appointment.createdAt(),
            appointment.updatedAt(),
            appointment.fullname(),
            appointment.email()
        );
    }

    public static AppointmentView toView(final AppointmentDocument document) {
        return new AppointmentView(
            document.getAppointmentId(),
            document.getPatientId(),
            document.getDoctorId(),
            document.getAppointmentDateTime(),
            document.getStatus(),
            document.getSourceEventId(),
            document.getIdempotencyKey(),
            document.getCreatedAt(),
            document.getUpdatedAt(),
            document.getFullname(),
            document.getEmail()
        );
    }

    public static AppointmentPage toPage(final Page<AppointmentDocument> page) {
        final List<AppointmentView> views = page.getContent().stream().map(AppointmentProjectionMapper::toView).toList();
        return new AppointmentPage(
            views,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext()
        );
    }
}
