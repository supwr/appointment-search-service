package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentProjectionDocument;
import com.appointmentsearch.api.domain.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AppointmentProjectionRepositoryCustom {
    Page<AppointmentProjectionDocument> search(
        UUID patientId,
        OffsetDateTime from,
        OffsetDateTime to,
        AppointmentStatus status,
        Pageable pageable
    );
}
