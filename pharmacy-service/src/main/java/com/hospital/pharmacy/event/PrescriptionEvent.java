package com.hospital.pharmacy.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento publicado en el topic "prescription.issued" cada vez que
 * se emite una receta médica. Es consumido, entre otros, por
 * notification-service para notificar al paciente por correo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionEvent implements Serializable {

    private String eventId;
    private String eventType;
    private String prescriptionCode;
    private Long patientId;
    private String patientEmail;
    private List<PrescriptionEventItem> items;
    private LocalDateTime issuedAt;
    private LocalDateTime timestamp;
}
