package com.hospital.reports.repository;

import com.hospital.reports.entity.AppointmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRecordRepository extends JpaRepository<AppointmentRecord, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    long countByStatus(String status);

    @Query("SELECT a.status AS status, COUNT(a) AS total FROM AppointmentRecord a GROUP BY a.status")
    List<StatusCount> countGroupedByStatus();

    interface StatusCount {
        String getStatus();
        Long getTotal();
    }
}
