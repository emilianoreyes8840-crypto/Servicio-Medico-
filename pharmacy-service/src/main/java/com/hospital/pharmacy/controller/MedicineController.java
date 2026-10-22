package com.hospital.pharmacy.controller;

import com.hospital.pharmacy.dto.MedicineRequestDTO;
import com.hospital.pharmacy.dto.MedicineResponseDTO;
import com.hospital.pharmacy.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    public ResponseEntity<MedicineResponseDTO> create(@Valid @RequestBody MedicineRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponseDTO>> getAll() {
        return ResponseEntity.ok(medicineService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<MedicineResponseDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(medicineService.getByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> update(@PathVariable Long id,
                                                        @Valid @RequestBody MedicineRequestDTO request) {
        return ResponseEntity.ok(medicineService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
