package uzumtech.notification.dto.price;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO для обновления цены
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PriceUpdateRequestDto {

    @Min(value = 0, message = "Price must be positive")
    private Long price;

    // Можно обновить только цену, isActive изменяется через отдельный endpoint
}
