package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.AppointmentProjectionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentProjectionRepository
    extends MongoRepository<AppointmentProjectionDocument, String>, AppointmentProjectionRepositoryCustom {
}
