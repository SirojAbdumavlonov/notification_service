package uzumtech.notification.entity;

public enum NotificationType {
    EMAIL,   // Отправка на email
    SMS,     // Отправка SMS
    PUSH     // Push-уведомление (Firebase, APNS и т.д.)
}