package uzumtech.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;     // кому отправить
    private String message;       // текст уведомления
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;     // EMAIL, SMS, PUSH

    @Enumerated(EnumType.STRING)
    private NotificationStatus status; // PENDING, SENT, FAILED

    // --- Constructors ---

    public Notification() {
    }

    public Notification(String recipient, String message, NotificationType type) {
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.status = NotificationStatus.PENDING;
    }

    // Установка createdAt автоматически перед сохранением
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters ---

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
