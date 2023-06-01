package proyecto.bootcamp.banca.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InputBankTransferDTO {
    private String originAccount;
    private String destinyAccount;
    private Double amount;
}
