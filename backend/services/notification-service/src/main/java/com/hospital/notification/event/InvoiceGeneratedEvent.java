package com.hospital.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento consumido desde el topic "invoice.generated",
 * publicado por el microservicio de facturación.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceGeneratedEvent {

    private String eventId;
    private Long invoiceId;
    private Long patientId;
    private String patientEmail;
    private BigDecimal totalAmount;
    private LocalDateTime issuedAt;
}
