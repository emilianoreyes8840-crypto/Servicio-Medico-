package com.hospital.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payload publicado en el topic Kafka 'invoice.generated'.
 * report-analyst-service mantiene un espejo exacto de esta clase
 * (com.hospital.reports.dto.InvoiceCreatedEvent) para deserializar el evento.
 * Si cambias un campo aqui, debes replicarlo alla o la deserializacion fallara.
 */
public class InvoiceCreatedEvent {

    private Long invoiceId;
    private BigDecimal monto;
    private LocalDateTime emittedAt;

    public InvoiceCreatedEvent() {
    }

    public InvoiceCreatedEvent(Long invoiceId, BigDecimal monto, LocalDateTime emittedAt) {
        this.invoiceId = invoiceId;
        this.monto = monto;
        this.emittedAt = emittedAt;
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
