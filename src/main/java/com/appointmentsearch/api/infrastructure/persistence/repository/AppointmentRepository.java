package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository
    extends MongoRepository<AppointmentDocument, String>, AppointmentRepositoryCustom {

    boolean existsByIdempotencyKey(String idempotencyKey);

}
