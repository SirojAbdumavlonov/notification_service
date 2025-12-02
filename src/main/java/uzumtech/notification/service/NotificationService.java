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

        //Сохраняем со статусом QUEUED
        notification.setStatus(NotificationStatus.QUEUED);
        Notification saved = repository.save(notification);

        //Формируем сообщение для Kafka
        NotificationSendRequestDto message = NotificationSendRequestDto.builder()
                .type(saved.getType())
                .title(saved.getTitle())
                .body(saved.getBody())
                .receiver(saved.getRecipient())
                .merchantId(saved.getMerchantId())
                .build();

        //Отправляем асинхронно и обрабатываем результат
        kafkaProducer.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        // Успешно отправлено в Kafka — можно обновить статус на SENT
                        // (делаем это в отдельной транзакции, чтобы не держать текущую открытой)
                        markAsSent(saved.getId());
                    } else {
                        // Ошибка — обновляем статус на FAILED
                        markAsFailed(saved.getId(), ex);
                    }
                });

        //Возвращаем сразу сохранённую сущность (со статусом QUEUED)
        return saved;
    }

    /**
     * Отдельные методы нужны, т.к. whenComplete выполняется в другом потоке
     * и не должен участвовать в текущей транзакции
     */
    @Transactional
    public void markAsSent(Long notificationId) {
        updateStatus(notificationId, NotificationStatus.SENT);
    }

    @Transactional
    public void markAsFailed(Long notificationId, Throwable ex) {
        updateStatus(notificationId, NotificationStatus.FAILED);
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
