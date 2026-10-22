package com.hospital.pharmacy.service;

import com.hospital.pharmacy.dto.*;
import com.hospital.pharmacy.entity.Medicine;
import com.hospital.pharmacy.entity.Prescription;
import com.hospital.pharmacy.event.PrescriptionEvent;
import com.hospital.pharmacy.event.PrescriptionEventItem;
import com.hospital.pharmacy.exception.InsufficientStockException;
import com.hospital.pharmacy.exception.ResourceNotFoundException;
import com.hospital.pharmacy.kafka.PrescriptionEventProducer;
import com.hospital.pharmacy.repository.MedicineRepository;
import com.hospital.pharmacy.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionEventProducer prescriptionEventProducer;

    /**
     * Procesa y emite una nueva receta:
     * 1) Valida existencia y stock de cada medicamento.
     * 2) Descuenta el stock correspondiente.
     * 3) Persiste la cabecera de la receta.
     * 4) Publica el evento PrescriptionEvent en el topic "prescription.issued".
     */
    public PrescriptionResponseDTO processPrescription(PrescriptionRequestDTO request) {

        List<PrescriptionItemDetailDTO> itemDetails = new ArrayList<>();
        List<PrescriptionEventItem> eventItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PrescriptionItemDTO item : request.getItems()) {
            Medicine medicine = medicineRepository.findByCode(item.getMedicineCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medicamento no encontrado con código: " + item.getMedicineCode()));

            if (medicine.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el medicamento " + medicine.getCode()
                                + ". Disponible: " + medicine.getStock() + ", solicitado: " + item.getQuantity());
            }

            medicine.setStock(medicine.getStock() - item.getQuantity());
            medicineRepository.save(medicine);

            BigDecimal subtotal = medicine.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            itemDetails.add(PrescriptionItemDetailDTO.builder()
                    .medicineCode(medicine.getCode())
                    .medicineName(medicine.getName())
                    .quantity(item.getQuantity())
                    .unitPrice(medicine.getUnitPrice())
                    .build());

            eventItems.add(PrescriptionEventItem.builder()
                    .medicineCode(medicine.getCode())
                    .medicineName(medicine.getName())
                    .quantity(item.getQuantity())
                    .unitPrice(medicine.getUnitPrice())
                    .build());
        }

        String prescriptionCode = generatePrescriptionCode();
        LocalDateTime now = LocalDateTime.now();

        Prescription prescription = Prescription.builder()
                .prescriptionCode(prescriptionCode)
                .patientId(request.getPatientId())
                .patientEmail(request.getPatientEmail())
                .issuedAt(now)
                .build();
        prescription = prescriptionRepository.save(prescription);

        PrescriptionEvent event = PrescriptionEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PRESCRIPTION_ISSUED")
                .prescriptionCode(prescription.getPrescriptionCode())
                .patientId(prescription.getPatientId())
                .patientEmail(prescription.getPatientEmail())
                .items(eventItems)
                .issuedAt(prescription.getIssuedAt())
                .timestamp(LocalDateTime.now())
                .build();

        prescriptionEventProducer.publishPrescriptionIssued(event);
        log.info("Receta {} emitida para paciente {}", prescription.getPrescriptionCode(), prescription.getPatientId());

        return PrescriptionResponseDTO.builder()
                .id(prescription.getId())
                .prescriptionCode(prescription.getPrescriptionCode())
                .patientId(prescription.getPatientId())
                .patientEmail(prescription.getPatientEmail())
                .issuedAt(prescription.getIssuedAt())
                .items(itemDetails)
                .totalAmount(totalAmount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<Prescription> getAll() {
        return prescriptionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Prescription getById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id: " + id));
    }

    private String generatePrescriptionCode() {
        return "RX-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
