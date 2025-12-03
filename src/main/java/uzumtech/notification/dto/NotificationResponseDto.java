package uzumtech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;

import java.time.LocalDateTime;

// DTO для возврата данных уведомления клиенту
// Не включаем Merchant entity - только merchantId
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationResponseDto {

    private Long id;

    private NotificationStatus status;

    private NotificationType type;

    private String title;

    private String body;

    private String imageUrl;

    private Long merchantId;

    private String receiverInfo;

    private String message;

    private Long price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
