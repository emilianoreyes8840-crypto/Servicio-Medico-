package com.hospital.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions", uniqueConstraints = @UniqueConstraint(columnNames = "prescription_code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_code", nullable = false, unique = true, length = 50)
    private String prescriptionCode;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "patient_email", nullable = false, length = 150)
    private String patientEmail;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
}
