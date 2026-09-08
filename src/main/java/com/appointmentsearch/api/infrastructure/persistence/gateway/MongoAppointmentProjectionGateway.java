package com.appointmentsearch.api.infrastructure.persistence.gateway;

import com.appointmentsearch.api.application.dto.search.AppointmentPage;
import com.appointmentsearch.api.application.dto.search.AppointmentSearchFilter;
import com.appointmentsearch.api.application.gateway.AppointmentProjectionGateway;
import com.appointmentsearch.api.domain.model.ScheduledAppointment;
import com.appointmentsearch.api.infrastructure.persistence.document.ProcessedEventDocument;
import com.appointmentsearch.api.infrastructure.persistence.mapper.AppointmentProjectionMapper;
import com.appointmentsearch.api.infrastructure.persistence.repository.AppointmentProjectionRepository;
import com.appointmentsearch.api.infrastructure.persistence.repository.ProcessedEventRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MongoAppointmentProjectionGateway implements AppointmentProjectionGateway {

    private final AppointmentProjectionRepository appointmentProjectionRepository;
    private final ProcessedEventRepository processedEventRepository;

    public MongoAppointmentProjectionGateway(
        final AppointmentProjectionRepository appointmentProjectionRepository,
        final ProcessedEventRepository processedEventRepository
    ) {
        this.appointmentProjectionRepository = appointmentProjectionRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    public void save(final ScheduledAppointment appointment) {
        appointmentProjectionRepository.save(AppointmentProjectionMapper.toDocument(appointment));
    }

    @Override
    public boolean isProcessed(final String idempotencyKey) {
        return processedEventRepository.existsById(idempotencyKey);
    }

    @Override
    public void markProcessed(final String idempotencyKey, final String eventId) {
        try {
            processedEventRepository.insert(new ProcessedEventDocument(idempotencyKey, eventId, Instant.now()));
        } catch (DuplicateKeyException ex) {
            return;
        }
    }

    @Override
    public AppointmentPage searchByPatient(final UUID patientId, final AppointmentSearchFilter filter) {
        final var pageable = PageRequest.of(
            filter.page(),
            filter.size(),
            Sort.by(Sort.Direction.DESC, "appointmentDateTime")
        );
        return AppointmentProjectionMapper.toPage(
            appointmentProjectionRepository.search(
                patientId,
                filter.from(),
                filter.to(),
                filter.status(),
                pageable
            )
        );
    }
}
