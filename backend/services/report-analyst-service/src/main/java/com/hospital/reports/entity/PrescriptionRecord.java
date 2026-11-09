package com.hospital.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Copia de solo lectura de cada evento 'prescription.issued' publicado por pharmacy-service.
 * NOTA: campos (prescriptionId, patientId, medicamento, emittedAt) son una suposicion
 * razonable; ajustalos al DTO real del productor.
 */
@Entity
@Table(name = "prescription_records")
public class PrescriptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_id", nullable = false, unique = true)
    private Long prescriptionId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "medicamento")
    private String medicamento;

    @Column(name = "emitted_at", nullable = false)
    private LocalDateTime emittedAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    public PrescriptionRecord() {
    }

    public PrescriptionRecord(Long prescriptionId, Long patientId, String medicamento, LocalDateTime emittedAt) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.medicamento = medicamento;
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

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
