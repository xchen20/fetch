package fetch;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Introspected
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @NotBlank
    @Pattern(regexp = "^[\\w\\s\\-]+$")
    private String shortDescription;

    @NotBlank
    @Pattern(regexp = "^\\d+\\.\\d{2}$")
    private String price;
}
