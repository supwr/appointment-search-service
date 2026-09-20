package com.appointmentsearch.api.infrastructure.persistence.gateway;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.application.gateway.AppointmentGateway;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.persistence.mapper.AppointmentProjectionMapper;
import com.appointmentsearch.api.infrastructure.persistence.repository.AppointmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MongoAppointmentGateway implements AppointmentGateway {

    private final AppointmentRepository appointmentRepository;

    public MongoAppointmentGateway(
        final AppointmentRepository appointmentRepository
    ) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void save(final ScheduledAppointment appointment) {
        appointmentRepository.save(AppointmentProjectionMapper.toDocument(appointment));
    }

    @Override
    public void deleteByAppointmentId(final UUID appointmentId) {
        appointmentRepository.deleteById(appointmentId.toString());
    }

    @Override
    public boolean isProcessed(final String idempotencyKey) {
        return appointmentRepository.existsByIdempotencyKey(idempotencyKey);
    }

    @Override
    public AppointmentPage searchByPatient(final UUID patientId, final AppointmentSearchFilter filter) {
        final var pageable = PageRequest.of(
            filter.page(),
            filter.size(),
            Sort.by(Sort.Direction.DESC, "appointmentDateTime")
        );
        return AppointmentProjectionMapper.toPage(
            appointmentRepository.search(
                patientId,
                filter.from(),
                filter.to(),
                pageable
            )
        );
    }
}
