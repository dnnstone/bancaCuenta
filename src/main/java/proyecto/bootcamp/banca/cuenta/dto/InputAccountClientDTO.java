package proyecto.bootcamp.banca.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputAccountClientDTO {
    private String clientId;
    private String typeAccountId;
    private Double initialAmount;
}
