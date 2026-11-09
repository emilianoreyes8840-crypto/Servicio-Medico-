package com.hospital.reports.repository;

import com.hospital.reports.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {

    boolean existsByPatientId(Long patientId);
}
