package uzumtech.notification.constant.enums;

public enum NotificationStatus {
    PENDING,   // уведомление создано и ожидает обработки Kafka Consumer
    QUEUED,    // отправлено в Kafka
    SENT,      // успешно отправлено
    FAILED     // отправка не удалась (ошибка)
}
