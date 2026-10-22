package com.hospital.notification.repository;

import com.hospital.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByEventId(String eventId);

    Optional<Notification> findByEventId(String eventId);
}
