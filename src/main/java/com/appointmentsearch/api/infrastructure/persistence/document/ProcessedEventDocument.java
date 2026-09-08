package com.appointmentsearch.api.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("processed_events")
public class ProcessedEventDocument {

    @Id
    private String id;

    private String eventId;
    private Instant processedAt;

    public ProcessedEventDocument() {
    }

    public ProcessedEventDocument(final String id, final String eventId, final Instant processedAt) {
        this.id = id;
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
