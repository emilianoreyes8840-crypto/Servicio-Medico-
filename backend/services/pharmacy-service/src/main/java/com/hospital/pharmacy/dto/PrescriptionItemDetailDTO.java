package com.hospital.pharmacy.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItemDetailDTO {

    private String medicineCode;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
