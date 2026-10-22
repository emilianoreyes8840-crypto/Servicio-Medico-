package com.hospital.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Evento consumido desde el topic "patient.registered",
 * publicado por el microservicio de pacientes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientRegisteredEvent {

    private String eventId;
    private Long patientId;
    private String patientEmail;
    private String patientName;
    private LocalDateTime registeredAt;
}
