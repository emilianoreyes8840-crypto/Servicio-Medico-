package com.hospital.reports.dto;

import java.time.LocalDateTime;

/**
 * SUPOSICION: estructura esperada del evento 'appointment.created' publicado por
 * appointment-service. Ajusta los nombres de campo para que coincidan exactamente
 * con el DTO productor real.
 */
public class AppointmentCreatedEvent {

    private Long appointmentId;
    private Long patientId;
    private String status;
    private LocalDateTime emittedAt;

    public AppointmentCreatedEvent() {
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
}
