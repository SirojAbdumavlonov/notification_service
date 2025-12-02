package uzumtech.notification.service;

import org.springframework.stereotype.Service;
import uzumtech.notification.entity.Price;
import uzumtech.notification.exception.notification.PriceNotFoundException;
import uzumtech.notification.repository.PriceRepository;

@Service
public class PriceService {

    private final PriceRepository repository;

    public PriceService(PriceRepository repository) {
        this.repository = repository;
    }

    public Price getActivePrice() {
        return repository.findByIsActiveTrue()
                .orElseThrow(() -> new PriceNotFoundException("Активный прайс не найден"));
    }
}
