package com.hospital.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Evento consumido desde el topic "appointment.created",
 * publicado por el microservicio de citas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentCreatedEvent {

    private String eventId;
    private Long appointmentId;
    private Long patientId;
    private String patientEmail;
    private String doctorName;
    private LocalDateTime appointmentDate;
}
