package uzumtech.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.exception.notification.NotificationNotFoundException;
import uzumtech.notification.repository.NotificationRepository;

/**
 * Сервис для обработки уведомлений и отправки событий в Kafka
 */
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationKafkaProducer kafkaProducer;

    public NotificationService(NotificationRepository repository, NotificationKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Отправить уведомление — сохраняет в БД и публикует событие в Kafka.
     */
    @Transactional
    public Notification send(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification не может быть null");
        }

        if (notification.getRecipient() == null || notification.getRecipient().isBlank()) {
            throw new IllegalArgumentException("Recipient не может быть пустым");
        }

        //Устанавливаем статус PENDING
        notification.setStatus(NotificationStatus.PENDING);

        //Сохраняем уведомление
        Notification saved = repository.save(notification);

        //Формируем DTO для Kafka
        NotificationSendRequestDto message = NotificationSendRequestDto.builder()
                .type(saved.getType())
                .title(saved.getTitle())
                .body(saved.getBody())
                .receiver(saved.getRecipient())
                .merchantId(saved.getMerchantId())
                .build();

        //Публикуем событие в Kafka
        kafkaProducer.send(message);

        //Обновляем статус на QUEUED (ожидает обработки)
        saved.setStatus(NotificationStatus.QUEUED);
        repository.save(saved);

        return saved;
    }

    /**
     * Обновить статус уведомления
     */
    @Transactional
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));

        notification.setStatus(status);
        return repository.save(notification);
    }

    /**
     * Получить уведомление по ID
     */
    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + id));
    }
}
