package com.hospital.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class InvoiceRequest {

    @NotNull(message = "patientId es obligatorio")
    private Long patientId;

    @NotNull(message = "medicalRecordId es obligatorio")
    private Long medicalRecordId;

    @NotBlank(message = "concepto es obligatorio")
    private String concepto;

    @NotNull(message = "monto es obligatorio")
    @Positive(message = "monto debe ser mayor a 0")
    private BigDecimal monto;

    public InvoiceRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(Long medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}
