package com.hospital.notification.service;

import com.hospital.notification.exception.EmailSendException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    /**
     * Envía un correo HTML mediante JavaMailSender (SMTP configurado para Mailtrap).
     * Lanza EmailSendException si el envío falla, para que la capa de
     * servicio pueda registrar el estado FAILED en la auditoría.
     */
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Correo enviado correctamente a {}", to);
        } catch (Exception ex) {
            log.error("Fallo al enviar correo a {}: {}", to, ex.getMessage(), ex);
            throw new EmailSendException("No se pudo enviar el correo a " + to, ex);
        }
    }
}
