package uzumtech.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uzumtech.notification.dto.NotificationResponseDto;
import uzumtech.notification.dto.NotificationSendRequestDto;
import uzumtech.notification.dto.ResponseDto;
import uzumtech.notification.entity.Notification;
import uzumtech.notification.mapper.NotificationMapper;
import uzumtech.notification.service.NotificationService;

/**
 * Контроллер для отправки уведомлений
 * Логирование вынесено в ControllerLoggingAspect
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /**
     * Отправить уведомление (добавляет в очередь Kafka)
     */
    @PostMapping("/send")
    public ResponseEntity<ResponseDto<Long>> sendNotification(@Valid @RequestBody NotificationSendRequestDto request) {
        // Сервис сам конвертирует DTO в entity и сохраняет
        Notification saved = notificationService.sendFromDto(request);

        // Возвращаем ID созданного уведомления
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.createSuccessResponse(saved.getId()));
    }

    // Получить уведомление по ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<NotificationResponseDto>> getNotification(@PathVariable Long id) {
        Notification notification = notificationService.findById(id);
        NotificationResponseDto responseDto = notificationMapper.toResponseDto(notification);

        return ResponseEntity.ok(ResponseDto.createSuccessResponse(responseDto));
    }
}
