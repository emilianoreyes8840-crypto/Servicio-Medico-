package com.hospital.reports.dto;

import java.time.LocalDateTime;

/**
 * SUPOSICION: estructura esperada del evento 'prescription.issued' publicado por
 * pharmacy-service. Ajusta los nombres de campo para que coincidan con el DTO real.
 */
public class PrescriptionIssuedEvent {

    private Long prescriptionId;
    private Long patientId;
    private String medicamento;
    private LocalDateTime emittedAt;

    public PrescriptionIssuedEvent() {
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public LocalDateTime getEmittedAt() {
        return emittedAt;
    }

    public void setEmittedAt(LocalDateTime emittedAt) {
        this.emittedAt = emittedAt;
    }
}
