package com.hospital.reports.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reports.dto.PatientRegisteredEvent;
import com.hospital.reports.entity.PatientRecord;
import com.hospital.reports.repository.PatientRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PatientEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PatientEventConsumer.class);

    private final PatientRecordRepository patientRecordRepository;
    private final ObjectMapper objectMapper;

    public PatientEventConsumer(PatientRecordRepository patientRecordRepository, ObjectMapper objectMapper) {
        this.patientRecordRepository = patientRecordRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "patient.registered", groupId = "report-analyst-service")
    public void consume(String payload) {
        try {
            PatientRegisteredEvent event = objectMapper.readValue(payload, PatientRegisteredEvent.class);

            if (patientRecordRepository.existsByPatientId(event.getPatientId())) {
                log.info("Evento de paciente duplicado ignorado, patientId={}", event.getPatientId());
                return;
            }

            PatientRecord record = new PatientRecord(event.getPatientId(), event.getNombre(), event.getEmittedAt());
            patientRecordRepository.save(record);

            log.info("Paciente replicado para reportes: patientId={}", event.getPatientId());
        } catch (Exception e) {
            log.error("Error procesando evento de 'patient.registered': {}", payload, e);
        }
    }
}
