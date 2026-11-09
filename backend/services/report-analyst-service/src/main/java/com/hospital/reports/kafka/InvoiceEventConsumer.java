package com.hospital.reports.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reports.dto.InvoiceCreatedEvent;
import com.hospital.reports.entity.InvoiceRecord;
import com.hospital.reports.repository.InvoiceRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventConsumer.class);

    private final InvoiceRecordRepository invoiceRecordRepository;
    private final ObjectMapper objectMapper;

    public InvoiceEventConsumer(InvoiceRecordRepository invoiceRecordRepository, ObjectMapper objectMapper) {
        this.invoiceRecordRepository = invoiceRecordRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "invoice.generated", groupId = "report-analyst-service")
    public void consume(String payload) {
        try {
            InvoiceCreatedEvent event = objectMapper.readValue(payload, InvoiceCreatedEvent.class);

            if (invoiceRecordRepository.existsByInvoiceId(event.getInvoiceId())) {
                log.info("Evento de factura duplicado ignorado, invoiceId={}", event.getInvoiceId());
                return;
            }

            InvoiceRecord record = new InvoiceRecord(event.getInvoiceId(), event.getMonto(), event.getEmittedAt());
            invoiceRecordRepository.save(record);

            log.info("Factura replicada para reportes: invoiceId={}, monto={}", event.getInvoiceId(), event.getMonto());
        } catch (Exception e) {
            log.error("Error procesando evento de 'invoice.generated': {}", payload, e);
        }
    }
}
