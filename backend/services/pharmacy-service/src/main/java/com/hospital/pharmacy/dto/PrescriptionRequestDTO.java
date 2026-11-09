package com.hospital.pharmacy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequestDTO {

    @NotNull(message = "El id del paciente es obligatorio")
    private Long patientId;

    @NotBlank(message = "El email del paciente es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String patientEmail;

    @NotEmpty(message = "La receta debe contener al menos un medicamento")
    @Valid
    private List<PrescriptionItemDTO> items;
}
