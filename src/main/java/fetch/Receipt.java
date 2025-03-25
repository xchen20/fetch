package fetch;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Serdeable
@Introspected
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    @NotBlank
    private String retailer;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String purchaseDate;

    @NotBlank
    @Pattern(regexp = "\\d{2}:\\d{2}")
    private String purchaseTime;

    private List<Item> items;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$")
    private String total;
}
