package com.hospital.billing.controller;

import com.hospital.billing.dto.InvoiceRequest;
import com.hospital.billing.entity.Invoice;
import com.hospital.billing.exception.ResourceNotFoundException;
import com.hospital.billing.kafka.InvoiceEventProducer;
import com.hospital.billing.repository.InvoiceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceRestController {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceEventProducer invoiceEventProducer;

    public InvoiceRestController(InvoiceRepository invoiceRepository, InvoiceEventProducer invoiceEventProducer) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceEventProducer = invoiceEventProducer;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        Invoice invoice = new Invoice(
                request.getPatientId(),
                request.getMedicalRecordId(),
                request.getConcepto(),
                request.getMonto()
        );

        Invoice saved = invoiceRepository.save(invoice);

        invoiceEventProducer.emitInvoiceGenerated(saved.getId(), saved.getMonto());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Si se envia ?estado=PAGADA filtra por ese estado (usa InvoiceRepository.findByEstado),
     * de lo contrario devuelve todas las facturas.
     */
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isBlank()) {
            return ResponseEntity.ok(invoiceRepository.findByEstado(estado.toUpperCase()));
        }
        return ResponseEntity.ok(invoiceRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una factura con id " + id));
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Invoice>> getInvoicesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(invoiceRepository.findByPatientId(patientId));
    }
}
