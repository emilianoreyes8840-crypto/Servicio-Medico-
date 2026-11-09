package com.hospital.pharmacy.event;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionEventItem implements Serializable {

    private String medicineCode;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
