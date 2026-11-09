package com.hospital.reports.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reports.dto.PrescriptionIssuedEvent;
import com.hospital.reports.entity.PrescriptionRecord;
import com.hospital.reports.repository.PrescriptionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionEventConsumer.class);

    private final PrescriptionRecordRepository prescriptionRecordRepository;
    private final ObjectMapper objectMapper;

    public PrescriptionEventConsumer(PrescriptionRecordRepository prescriptionRecordRepository, ObjectMapper objectMapper) {
        this.prescriptionRecordRepository = prescriptionRecordRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "prescription.issued", groupId = "report-analyst-service")
    public void consume(String payload) {
        try {
            PrescriptionIssuedEvent event = objectMapper.readValue(payload, PrescriptionIssuedEvent.class);

            if (prescriptionRecordRepository.existsByPrescriptionId(event.getPrescriptionId())) {
                log.info("Evento de receta duplicado ignorado, prescriptionId={}", event.getPrescriptionId());
                return;
            }

            PrescriptionRecord record = new PrescriptionRecord(
                    event.getPrescriptionId(), event.getPatientId(), event.getMedicamento(), event.getEmittedAt());
            prescriptionRecordRepository.save(record);

            log.info("Receta replicada para reportes: prescriptionId={}, patientId={}",
                    event.getPrescriptionId(), event.getPatientId());
        } catch (Exception e) {
            log.error("Error procesando evento de 'prescription.issued': {}", payload, e);
        }
    }
}
