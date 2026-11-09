package com.hospital.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Copia de solo lectura de cada evento 'appointment.created' publicado por appointment-service.
 * NOTA: los nombres de campos del evento son una suposicion razonable (appointmentId, patientId,
 * status, emittedAt); ajustalos para que calcen exactamente con el DTO real que publica
 * appointment-service.
 */
@Entity
@Table(name = "appointment_records")
public class AppointmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "emitted_at", nullable = false)
    private LocalDateTime emittedAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    public AppointmentRecord() {
    }

    public AppointmentRecord(Long appointmentId, Long patientId, String status, LocalDateTime emittedAt) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.status = status;
        this.emittedAt = emittedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.receivedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEmittedAt() {
        return emittedAt;
    }

    public void setEmittedAt(LocalDateTime emittedAt) {
        this.emittedAt = emittedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
