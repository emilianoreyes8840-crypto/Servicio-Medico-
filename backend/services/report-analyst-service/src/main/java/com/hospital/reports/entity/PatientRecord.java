package com.hospital.reports.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Copia de solo lectura de cada evento 'patient.registered' publicado por patient-service.
 * NOTA: campo 'nombre' incluido solo como referencia opcional; si el evento real de
 * patient-service no lo trae, elimina la columna o ajustala.
 */
@Entity
@Table(name = "patient_records")
public class PatientRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false, unique = true)
    private Long patientId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "emitted_at", nullable = false)
    private LocalDateTime emittedAt;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    public PatientRecord() {
    }

    public PatientRecord(Long patientId, String nombre, LocalDateTime emittedAt) {
        this.patientId = patientId;
        this.nombre = nombre;
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

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
