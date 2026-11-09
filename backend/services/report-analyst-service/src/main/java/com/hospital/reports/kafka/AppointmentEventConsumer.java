package com.hospital.reports.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.reports.dto.AppointmentCreatedEvent;
import com.hospital.reports.entity.AppointmentRecord;
import com.hospital.reports.repository.AppointmentRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventConsumer.class);

    private final AppointmentRecordRepository appointmentRecordRepository;
    private final ObjectMapper objectMapper;

    public AppointmentEventConsumer(AppointmentRecordRepository appointmentRecordRepository, ObjectMapper objectMapper) {
        this.appointmentRecordRepository = appointmentRecordRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "appointment.created", groupId = "report-analyst-service")
    public void consume(String payload) {
        try {
            AppointmentCreatedEvent event = objectMapper.readValue(payload, AppointmentCreatedEvent.class);

            if (appointmentRecordRepository.existsByAppointmentId(event.getAppointmentId())) {
                log.info("Evento de cita duplicado ignorado, appointmentId={}", event.getAppointmentId());
                return;
            }

            AppointmentRecord record = new AppointmentRecord(
                    event.getAppointmentId(), event.getPatientId(), event.getStatus(), event.getEmittedAt());
            appointmentRecordRepository.save(record);

            log.info("Cita replicada para reportes: appointmentId={}, patientId={}",
                    event.getAppointmentId(), event.getPatientId());
        } catch (Exception e) {
            log.error("Error procesando evento de 'appointment.created': {}", payload, e);
        }
    }
}
