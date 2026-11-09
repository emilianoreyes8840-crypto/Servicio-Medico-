package com.hospital.pharmacy.service;

import com.hospital.pharmacy.dto.MedicineRequestDTO;
import com.hospital.pharmacy.dto.MedicineResponseDTO;
import com.hospital.pharmacy.entity.Medicine;
import com.hospital.pharmacy.exception.DuplicateResourceException;
import com.hospital.pharmacy.exception.ResourceNotFoundException;
import com.hospital.pharmacy.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineResponseDTO create(MedicineRequestDTO request) {
        if (medicineRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe un medicamento con el código: " + request.getCode());
        }
        Medicine medicine = Medicine.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .stock(request.getStock())
                .unitPrice(request.getUnitPrice())
                .build();
        return toResponse(medicineRepository.save(medicine));
    }

    @Transactional(readOnly = true)
    public List<MedicineResponseDTO> getAll() {
        return medicineRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicineResponseDTO getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public MedicineResponseDTO getByCode(String code) {
        Medicine medicine = medicineRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento no encontrado con código: " + code));
        return toResponse(medicine);
    }

    public MedicineResponseDTO update(Long id, MedicineRequestDTO request) {
        Medicine medicine = findEntityById(id);

        if (!medicine.getCode().equals(request.getCode()) && medicineRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe un medicamento con el código: " + request.getCode());
        }

        medicine.setCode(request.getCode());
        medicine.setName(request.getName());
        medicine.setDescription(request.getDescription());
        medicine.setStock(request.getStock());
        medicine.setUnitPrice(request.getUnitPrice());

        return toResponse(medicineRepository.save(medicine));
    }

    public void delete(Long id) {
        Medicine medicine = findEntityById(id);
        medicineRepository.delete(medicine);
    }

    private Medicine findEntityById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento no encontrado con id: " + id));
    }

    private MedicineResponseDTO toResponse(Medicine medicine) {
        return MedicineResponseDTO.builder()
                .id(medicine.getId())
                .code(medicine.getCode())
                .name(medicine.getName())
                .description(medicine.getDescription())
                .stock(medicine.getStock())
                .unitPrice(medicine.getUnitPrice())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
