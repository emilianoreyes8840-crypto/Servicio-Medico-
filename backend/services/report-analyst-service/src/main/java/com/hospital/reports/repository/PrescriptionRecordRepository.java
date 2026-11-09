package com.hospital.reports.repository;

import com.hospital.reports.entity.PrescriptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRecordRepository extends JpaRepository<PrescriptionRecord, Long> {

    boolean existsByPrescriptionId(Long prescriptionId);
}
