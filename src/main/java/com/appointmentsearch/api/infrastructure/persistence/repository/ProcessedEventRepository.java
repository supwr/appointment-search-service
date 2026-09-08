package com.appointmentsearch.api.infrastructure.persistence.repository;

import com.appointmentsearch.api.infrastructure.persistence.document.ProcessedEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedEventRepository extends MongoRepository<ProcessedEventDocument, String> {
}
