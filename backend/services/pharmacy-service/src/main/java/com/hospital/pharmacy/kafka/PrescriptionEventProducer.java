package com.hospital.pharmacy.kafka;

import com.hospital.pharmacy.event.PrescriptionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrescriptionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.prescription-issued}")
    private String prescriptionIssuedTopic;

    public void publishPrescriptionIssued(PrescriptionEvent event) {
        kafkaTemplate.send(prescriptionIssuedTopic, event.getPrescriptionCode(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento PrescriptionEvent publicado correctamente. topic={}, key={}, offset={}",
                                prescriptionIssuedTopic,
                                event.getPrescriptionCode(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Error publicando PrescriptionEvent en topic={}, key={}: {}",
                                prescriptionIssuedTopic, event.getPrescriptionCode(), ex.getMessage(), ex);
                    }
                });
    }
}
