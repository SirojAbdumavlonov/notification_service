package uzumtech.notification.dto.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// DTO для возврата информации о цене
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PriceResponseDto {

    private Long id;

    // Цена за 1 SMS (в сумах)
    private Long price;

    // Активна ли эта цена
    private boolean isActive;

    // Дата создания цены
    private LocalDateTime createdAt;
}
