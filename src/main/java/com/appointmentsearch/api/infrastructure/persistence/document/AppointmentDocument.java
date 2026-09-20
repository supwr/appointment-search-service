package com.appointmentsearch.api.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document("appointments")
public class AppointmentDocument {

    @Id
    private String id;

    @Indexed
    private UUID appointmentId;

    @Indexed
    private UUID patientId;

    @Indexed
    private UUID doctorId;

    @Indexed
    private Instant appointmentDateTime;

    private String sourceEventId;
    private String idempotencyKey;
    private Instant createdAt;
    private Instant updatedAt;
    private String fullname;
    private String email;

    public AppointmentDocument() {
    }

    public AppointmentDocument(
        final String id,
        final UUID appointmentId,
        final UUID patientId,
        final UUID doctorId,
        final Instant appointmentDateTime,
        final String sourceEventId,
        final String idempotencyKey,
        final Instant createdAt,
        final Instant updatedAt,
        final String fullname,
        final String email
    ) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.sourceEventId = sourceEventId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fullname = fullname;
        this.email = email;
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

    public Instant getAppointmentDateTime() {
        return appointmentDateTime;
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

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }
}
