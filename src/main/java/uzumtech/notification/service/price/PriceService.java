package uzumtech.notification.service.price;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.PriceNotFoundException;
import uzumtech.notification.repository.PriceRepository;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    /**
     * Получить активный прайс (например, 85 сум за 1 SMS)
     */
    public Price getActivePrice() {
        return priceRepository.findByIsActiveTrue()
                .orElseThrow(() -> new PriceNotFoundException("Активный прайс не найден"));
    }

    /**
     * Получить стоимость одного SMS
     */
    public Long getPricePerSms() {
        return getActivePrice().getPrice();
    }
    // Будут доработки на случай изменения трафика в середине месяца
}
