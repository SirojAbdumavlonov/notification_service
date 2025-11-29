package uzumtech.notification.service;

import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Сервис для уведомлений: сохраняет их в базу и отправляет в Kafka
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationKafkaProducer kafkaProducer;

    public NotificationService(NotificationRepository repository,
                               NotificationKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    // Добавляет новое уведомление, ставит статус PENDING и отправляет в Kafka
    @Transactional
    public Notification queue(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification не может быть null");
        }

        // Ставим статус PENDING
        notification.setStatus(NotificationStatus.PENDING);

        // Сохраняем уведомление в базе
        Notification saved = repository.save(notification);

        // Отправляем уведомление в Kafka
        kafkaProducer.send(saved);

        return saved;
    }

    // Обновляет статус уведомления (например SENT или FAILED)
    @Transactional
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Уведомление не найдено с id: " + id));

        notification.setStatus(status);
        return repository.save(notification);
    }

    // Находит уведомление по id
    public Notification findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Уведомление не найдено с id: " + id));
    }
}
