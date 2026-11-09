package com.hospital.reports.controller;

import com.hospital.reports.repository.AppointmentRecordRepository;
import com.hospital.reports.repository.InvoiceRecordRepository;
import com.hospital.reports.repository.PatientRecordRepository;
import com.hospital.reports.repository.PrescriptionRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class ReportDashboardController {

    private final InvoiceRecordRepository invoiceRecordRepository;
    private final AppointmentRecordRepository appointmentRecordRepository;
    private final PatientRecordRepository patientRecordRepository;
    private final PrescriptionRecordRepository prescriptionRecordRepository;

    public ReportDashboardController(InvoiceRecordRepository invoiceRecordRepository,
                                      AppointmentRecordRepository appointmentRecordRepository,
                                      PatientRecordRepository patientRecordRepository,
                                      PrescriptionRecordRepository prescriptionRecordRepository) {
        this.invoiceRecordRepository = invoiceRecordRepository;
        this.appointmentRecordRepository = appointmentRecordRepository;
        this.patientRecordRepository = patientRecordRepository;
        this.prescriptionRecordRepository = prescriptionRecordRepository;
    }

    @GetMapping("/reportes")
    public String dashboard(Model model) {
        model.addAttribute("totalFacturas", invoiceRecordRepository.count());
        model.addAttribute("ingresosTotales", invoiceRecordRepository.sumMonto());
        model.addAttribute("totalCitas", appointmentRecordRepository.count());
        model.addAttribute("totalPacientes", patientRecordRepository.count());
        model.addAttribute("totalRecetas", prescriptionRecordRepository.count());
        return "reports";
    }
}
