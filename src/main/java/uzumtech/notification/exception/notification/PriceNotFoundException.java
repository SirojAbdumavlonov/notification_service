package uzumtech.notification.exception.notification;

import org.springframework.http.HttpStatus;
import uzumtech.notification.exception.NotificationException;

// Исключение когда не найдена активная цена для SMS
public class PriceNotFoundException extends NotificationException {

    public PriceNotFoundException(String message) {
        super(message, "PRICE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
