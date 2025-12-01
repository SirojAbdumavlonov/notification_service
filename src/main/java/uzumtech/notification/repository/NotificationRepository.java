package uzumtech.notification.repository;

import uzumtech.notification.constant.enums.NotificationStatus;
import uzumtech.notification.constant.enums.NotificationType;
import uzumtech.notification.entity.Notification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository для работы с уведомлениями
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>,
        JpaSpecificationExecutor<Notification> {

    // Поиск по статусу
    List<Notification> findByStatus(NotificationStatus status);

    // Поиск по типу
    List<Notification> findByType(NotificationType type);

    // Поиск по статусу с сортировкой
    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    // Поиск по типу и статусу
    List<Notification> findByTypeAndStatus(NotificationType type, NotificationStatus status);

    // Поиск для повторной отправки
    @Query("""
            SELECT n FROM Notification n
            WHERE n.status = :status
            ORDER BY n.createdAt ASC
            """)
    List<Notification> findForRetry(NotificationStatus status);

    // Обновление статуса уведомления
    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.status = :status
            WHERE n.id = :id
            """)
    void updateStatus(Long id, NotificationStatus status);
}
