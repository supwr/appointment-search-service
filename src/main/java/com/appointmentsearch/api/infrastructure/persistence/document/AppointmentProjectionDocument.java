package com.appointmentsearch.api.infrastructure.persistence.document;

import com.appointmentsearch.api.domain.model.AppointmentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Document("appointments")
public class AppointmentProjectionDocument {

    @Id
    private String id;

    @Indexed
    private UUID appointmentId;

    @Indexed
    private UUID patientId;

    @Indexed
    private UUID doctorId;

    @Indexed
    private OffsetDateTime appointmentDateTime;

    @Indexed
    private AppointmentStatus status;

    private String sourceEventId;
    private String idempotencyKey;
    private Instant createdAt;
    private Instant updatedAt;

    public AppointmentProjectionDocument() {
    }

    public AppointmentProjectionDocument(
        final String id,
        final UUID appointmentId,
        final UUID patientId,
        final UUID doctorId,
        final OffsetDateTime appointmentDateTime,
        final AppointmentStatus status,
        final String sourceEventId,
        final String idempotencyKey,
        final Instant createdAt,
        final Instant updatedAt
    ) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.sourceEventId = sourceEventId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public OffsetDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getSourceEventId() {
        return sourceEventId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
