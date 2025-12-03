package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.repository.NotificationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// Сервис для расчета стоимости SMS уведомлений мерчанта
// Считает по фактическим ценам из каждого уведомления
@Service
@RequiredArgsConstructor
public class MerchantSmsBillingService {

    private final NotificationRepository notificationRepository;

    // Рассчитать сумму за отправленные SMS уведомления мерчанта за период
    // Учитывает изменение цены в середине периода - берет цену из каждого уведомления
    public Long calculateSmsCost(Long merchantId, LocalDate from, LocalDate to) {
        // Валидация параметров
        if (merchantId == null) {
            throw new IllegalArgumentException("merchantId не может быть null");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Даты from и to не могут быть null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата from должна быть раньше или равна to");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        // Получаем только отправленные SMS уведомления
        List<Notification> sentSms = notificationRepository
                .findAllByMerchant_IdAndTypeAndStatusAndCreatedAtBetween(
                        merchantId,
                        NotificationType.SMS,
                        NotificationStatus.SENT,
                        start,
                        end
                );

        // Суммируем цены ИЗ уведомлений (учитывает изменение цены!)
        // Пример: 10 SMS по 85 сум + 5 SMS по 100 сум = 850 + 500 = 1350 сум
        return sentSms.stream()
                .mapToLong(Notification::getPrice)
                .sum();
    }

    // Получить количество отправленных SMS за период
    public long getSmsCount(Long merchantId, LocalDate from, LocalDate to) {
        // Валидация параметров
        if (merchantId == null) {
            throw new IllegalArgumentException("merchantId не может быть null");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Даты from и to не могут быть null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата from должна быть раньше или равна to");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        return notificationRepository
                .findAllByMerchant_IdAndTypeAndStatusAndCreatedAtBetween(
                        merchantId,
                        NotificationType.SMS,
                        NotificationStatus.SENT,
                        start,
                        end
                )
                .size();
    }
}

