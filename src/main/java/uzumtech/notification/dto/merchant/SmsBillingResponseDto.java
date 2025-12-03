package uzumtech.notification.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// DTO для ответа с информацией о биллинге SMS
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SmsBillingResponseDto {

    private Long merchantId;

    // Общая стоимость отправленных SMS за период
    private Long totalCost;

    // Количество отправленных SMS
    private Long smsCount;

    // Период, за который считается биллинг
    private LocalDate periodFrom;
    private LocalDate periodTo;
}
