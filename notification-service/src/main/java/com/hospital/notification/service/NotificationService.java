package com.hospital.notification.service;

import com.hospital.notification.entity.Notification;
import com.hospital.notification.entity.NotificationStatus;
import com.hospital.notification.exception.EmailSendException;
import com.hospital.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /**
     * Procesa un evento de negocio recibido por Kafka:
     *  1) Verifica idempotencia por eventId (evita duplicados si el
     *     mensaje se reprocesa o llega más de una vez).
     *  2) Crea el registro de auditoría en estado PENDING.
     *  3) Intenta enviar el correo; actualiza a SENT o FAILED según el resultado.
     *
     * Los errores de envío se capturan y quedan registrados en la
     * base de datos (status=FAILED, errorMessage) sin relanzar la
     * excepción, para no bloquear el offset del consumer de Kafka.
     */
    @Transactional
    public void processEvent(String eventType, String eventId, String recipientEmail,
                              String subject, String content) {

        if (notificationRepository.existsByEventId(eventId)) {
            log.warn("Evento duplicado ignorado. eventType={}, eventId={}", eventType, eventId);
            return;
        }

        Notification notification = Notification.builder()
                .eventType(eventType)
                .eventId(eventId)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();
        notification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(recipientEmail, subject, content);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setErrorMessage(null);
        } catch (EmailSendException ex) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(ex.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            log.error("Notificación marcada como FAILED. eventType={}, eventId={}", eventType, eventId);
        }

        notificationRepository.save(notification);
    }
}
