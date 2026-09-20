package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AppointmentRepositoryCustom {
    Page<AppointmentDocument> search(
        UUID patientId,
        OffsetDateTime from,
        OffsetDateTime to,
        Pageable pageable
    );
}
