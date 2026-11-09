package com.hospital.reports.dto;

import java.time.LocalDateTime;

/**
 * SUPOSICION: estructura esperada del evento 'patient.registered' publicado por
 * patient-service. Ajusta los nombres de campo para que coincidan con el DTO real.
 */
public class PatientRegisteredEvent {

    private Long patientId;
    private String nombre;
    private LocalDateTime emittedAt;

    public PatientRegisteredEvent() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getEmittedAt() {
        return emittedAt;
    }

    public void setEmittedAt(LocalDateTime emittedAt) {
        this.emittedAt = emittedAt;
    }
}
