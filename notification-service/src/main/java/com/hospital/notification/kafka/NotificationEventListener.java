package com.hospital.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.notification.event.AppointmentCreatedEvent;
import com.hospital.notification.event.InvoiceGeneratedEvent;
import com.hospital.notification.event.PatientRegisteredEvent;
import com.hospital.notification.event.PrescriptionIssuedEvent;
import com.hospital.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @KafkaListener(topics = "${app.kafka.topics.patient-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPatientRegistered(String message) {
        try {
            PatientRegisteredEvent event = objectMapper.readValue(message, PatientRegisteredEvent.class);
            String subject = "¡Bienvenido/a al Sistema Hospitalario!";
            String content = "<h2>Hola " + safe(event.getPatientName()) + "</h2>"
                    + "<p>Tu registro como paciente se ha completado exitosamente.</p>"
                    + "<p>Tu ID de paciente es: <b>" + event.getPatientId() + "</b></p>"
                    + "<p>Gracias por confiar en nosotros.</p>";

            notificationService.processEvent("PATIENT_REGISTERED", event.getEventId(),
                    event.getPatientEmail(), subject, content);
        } catch (Exception ex) {
            log.error("Error procesando evento de patient.registered: {}", ex.getMessage(), ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.appointment-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void onAppointmentCreated(String message) {
        try {
            AppointmentCreatedEvent event = objectMapper.readValue(message, AppointmentCreatedEvent.class);
            String subject = "Cita médica confirmada";
            String content = "<h2>Tu cita ha sido agendada</h2>"
                    + "<p>Doctor(a): <b>" + safe(event.getDoctorName()) + "</b></p>"
                    + "<p>Fecha: <b>" + (event.getAppointmentDate() != null ? event.getAppointmentDate().format(FMT) : "N/D") + "</b></p>"
                    + "<p>Por favor llega 15 minutos antes de tu horario.</p>";

            notificationService.processEvent("APPOINTMENT_CREATED", event.getEventId(),
                    event.getPatientEmail(), subject, content);
        } catch (Exception ex) {
            log.error("Error procesando evento de appointment.created: {}", ex.getMessage(), ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.invoice-generated}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInvoiceGenerated(String message) {
        try {
            InvoiceGeneratedEvent event = objectMapper.readValue(message, InvoiceGeneratedEvent.class);
            String subject = "Factura generada - Sistema Hospitalario";
            String content = "<h2>Se generó una nueva factura</h2>"
                    + "<p>Número de factura: <b>" + event.getInvoiceId() + "</b></p>"
                    + "<p>Monto total: <b>$" + event.getTotalAmount() + "</b></p>"
                    + "<p>Gracias por tu preferencia.</p>";

            notificationService.processEvent("INVOICE_GENERATED", event.getEventId(),
                    event.getPatientEmail(), subject, content);
        } catch (Exception ex) {
            log.error("Error procesando evento de invoice.generated: {}", ex.getMessage(), ex);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.prescription-issued}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPrescriptionIssued(String message) {
        try {
            PrescriptionIssuedEvent event = objectMapper.readValue(message, PrescriptionIssuedEvent.class);

            StringBuilder itemsHtml = new StringBuilder("<ul>");
            if (event.getItems() != null) {
                for (PrescriptionIssuedEvent.Item item : event.getItems()) {
                    itemsHtml.append("<li>")
                            .append(item.getMedicineName())
                            .append(" x ")
                            .append(item.getQuantity())
                            .append("</li>");
                }
            }
            itemsHtml.append("</ul>");

            String subject = "Receta médica emitida - " + event.getPrescriptionCode();
            String content = "<h2>Se emitió una nueva receta médica</h2>"
                    + "<p>Código de receta: <b>" + event.getPrescriptionCode() + "</b></p>"
                    + "<p>Medicamentos recetados:</p>"
                    + itemsHtml
                    + "<p>Puedes recogerla en la farmacia del hospital.</p>";

            notificationService.processEvent("PRESCRIPTION_ISSUED", event.getEventId(),
                    event.getPatientEmail(), subject, content);
        } catch (Exception ex) {
            log.error("Error procesando evento de prescription.issued: {}", ex.getMessage(), ex);
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
