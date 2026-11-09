package com.hospital.pharmacy.controller;

import com.hospital.pharmacy.dto.PrescriptionRequestDTO;
import com.hospital.pharmacy.dto.PrescriptionResponseDTO;
import com.hospital.pharmacy.entity.Prescription;
import com.hospital.pharmacy.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/prescriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    /**
     * Procesa y emite una receta médica. Descuenta stock y publica
     * el evento PrescriptionEvent en Kafka (topic prescription.issued).
     */
    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO> processPrescription(
            @Valid @RequestBody PrescriptionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prescriptionService.processPrescription(request));
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getAll() {
        return ResponseEntity.ok(prescriptionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getById(id));
    }
}