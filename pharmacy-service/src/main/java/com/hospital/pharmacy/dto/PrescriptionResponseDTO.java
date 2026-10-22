package com.hospital.pharmacy.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponseDTO {

    private Long id;
    private String prescriptionCode;
    private Long patientId;
    private String patientEmail;
    private LocalDateTime issuedAt;
    private List<PrescriptionItemDetailDTO> items;
    private BigDecimal totalAmount;
}
