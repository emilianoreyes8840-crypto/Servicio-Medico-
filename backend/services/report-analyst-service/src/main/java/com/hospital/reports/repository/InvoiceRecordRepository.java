package com.hospital.reports.repository;

import com.hospital.reports.entity.InvoiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface InvoiceRecordRepository extends JpaRepository<InvoiceRecord, Long> {

    boolean existsByInvoiceId(Long invoiceId);

    @Query("SELECT COALESCE(SUM(i.monto), 0) FROM InvoiceRecord i")
    BigDecimal sumMonto();

    @Query("SELECT COALESCE(AVG(i.monto), 0) FROM InvoiceRecord i")
    Optional<BigDecimal> averageMonto();
}
