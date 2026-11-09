package com.hospital.billing.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.billing.dto.InvoiceCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class InvoiceEventProducer {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventProducer.class);
    private static final String TOPIC = "invoice.generated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InvoiceEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void emitInvoiceGenerated(Long invoiceId, BigDecimal monto) {
        InvoiceCreatedEvent event = new InvoiceCreatedEvent(invoiceId, monto, LocalDateTime.now());

        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error serializando InvoiceCreatedEvent para invoiceId={}", invoiceId, e);
            return;
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(TOPIC, invoiceId.toString(), payload);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento publicado en topic '{}' -> partition={}, offset={}, key={}",
                        TOPIC,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        invoiceId);
            } else {
                log.error("Fallo al publicar evento en topic '{}' para invoiceId={}", TOPIC, invoiceId, ex);
            }
        });
    }
}
