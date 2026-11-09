package com.hospital.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento consumido desde el topic "prescription.issued",
 * publicado por pharmacy-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrescriptionIssuedEvent {

    private String eventId;
    private String eventType;
    private String prescriptionCode;
    private Long patientId;
    private String patientEmail;
    private List<Item> items;
    private LocalDateTime issuedAt;
    private LocalDateTime timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String medicineCode;
        private String medicineName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
