package com.hospital.reports.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Debe ser un espejo exacto del evento que publica billing-service
 * (com.hospital.billing.dto.InvoiceCreatedEvent) en el topic 'invoice.generated'.
 */
public class InvoiceCreatedEvent {

    private Long invoiceId;
    private BigDecimal monto;
    private LocalDateTime emittedAt;

    public InvoiceCreatedEvent() {
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getEmittedAt() {
        return emittedAt;
    }

    public void setEmittedAt(LocalDateTime emittedAt) {
        this.emittedAt = emittedAt;
    }
}
