package com.hospital.reports.controller;

import com.hospital.reports.client.MedicalRecordClient;
import com.hospital.reports.dto.ReportSummaryResponse;
import com.hospital.reports.entity.AppointmentRecord;
import com.hospital.reports.entity.InvoiceRecord;
import com.hospital.reports.entity.PrescriptionRecord;
import com.hospital.reports.repository.AppointmentRecordRepository;
import com.hospital.reports.repository.InvoiceRecordRepository;
import com.hospital.reports.repository.PatientRecordRepository;
import com.hospital.reports.repository.PrescriptionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    private static final Logger log = LoggerFactory.getLogger(ReportRestController.class);

    private final InvoiceRecordRepository invoiceRecordRepository;
    private final AppointmentRecordRepository appointmentRecordRepository;
    private final PatientRecordRepository patientRecordRepository;
    private final PrescriptionRecordRepository prescriptionRecordRepository;
    private final MedicalRecordClient medicalRecordClient;

    public ReportRestController(InvoiceRecordRepository invoiceRecordRepository,
                                 AppointmentRecordRepository appointmentRecordRepository,
                                 PatientRecordRepository patientRecordRepository,
                                 PrescriptionRecordRepository prescriptionRecordRepository,
                                 MedicalRecordClient medicalRecordClient) {
        this.invoiceRecordRepository = invoiceRecordRepository;
        this.appointmentRecordRepository = appointmentRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
        this.prescriptionRecordRepository = prescriptionRecordRepository;
        this.medicalRecordClient = medicalRecordClient;
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getSummary() {
        ReportSummaryResponse summary = new ReportSummaryResponse();

        long totalFacturas = invoiceRecordRepository.count();
        BigDecimal ingresosTotales = invoiceRecordRepository.sumMonto();
        summary.setTotalFacturas(totalFacturas);
        summary.setIngresosTotales(ingresosTotales);
        summary.setIngresoPromedioPorFactura(invoiceRecordRepository.averageMonto().orElse(BigDecimal.ZERO));

        summary.setTotalCitas(appointmentRecordRepository.count());
        Map<String, Long> citasPorEstado = appointmentRecordRepository.countGroupedByStatus().stream()
                .collect(Collectors.toMap(
                        AppointmentRecordRepository.StatusCount::getStatus,
                        AppointmentRecordRepository.StatusCount::getTotal));
        summary.setCitasPorEstado(citasPorEstado);

        summary.setTotalPacientesRegistrados(patientRecordRepository.count());
        summary.setTotalRecetasEmitidas(prescriptionRecordRepository.count());

        summary.setTotalExpedientesMedicos(fetchMedicalRecordsCountSafely());

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/billing")
    public ResponseEntity<List<InvoiceRecord>> getBillingRecords() {
        return ResponseEntity.ok(invoiceRecordRepository.findAll());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentRecord>> getAppointmentRecords() {
        return ResponseEntity.ok(appointmentRecordRepository.findAll());
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<List<PrescriptionRecord>> getPrescriptionRecords() {
        return ResponseEntity.ok(prescriptionRecordRepository.findAll());
    }

    /**
     * Llamada sincrona de ejemplo via Feign + Eureka hacia medical-record-service.
     * Si el servicio no esta disponible o el endpoint no existe todavia, no se rompe
     * el reporte completo: se registra el error y se devuelve null en ese campo.
     */
    private Long fetchMedicalRecordsCountSafely() {
        try {
            return medicalRecordClient.countMedicalRecords();
        } catch (Exception e) {
            log.warn("No se pudo obtener el conteo de expedientes medicos via Feign: {}", e.getMessage());
            return null;
        }
    }
}
