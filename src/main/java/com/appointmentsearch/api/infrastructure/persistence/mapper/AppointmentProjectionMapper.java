package com.appointmentsearch.api.infrastructure.persistence.mapper;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentView;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentProjectionDocument;
import org.springframework.data.domain.Page;

import java.util.List;

public final class AppointmentProjectionMapper {

    private AppointmentProjectionMapper() {
    }

    public static AppointmentProjectionDocument toDocument(final ScheduledAppointment appointment) {
        return new AppointmentProjectionDocument(
            appointment.appointmentId().toString(),
            appointment.appointmentId(),
            appointment.patientId(),
            appointment.doctorId(),
            appointment.appointmentDateTime(),
            appointment.status(),
            appointment.sourceEventId(),
            appointment.idempotencyKey(),
            appointment.createdAt(),
            appointment.updatedAt()
        );
    }

    public static AppointmentView toView(final AppointmentProjectionDocument document) {
        return new AppointmentView(
            document.getAppointmentId(),
            document.getPatientId(),
            document.getDoctorId(),
            document.getAppointmentDateTime(),
            document.getStatus(),
            document.getSourceEventId(),
            document.getIdempotencyKey(),
            document.getCreatedAt(),
            document.getUpdatedAt()
        );
    }

    public static AppointmentPage toPage(final Page<AppointmentProjectionDocument> page) {
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
