package proyecto.bootcamp.banca.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.bootcamp.banca.cuenta.model.DebitCard;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputDebitCar {
    private DebitCard debitCard;
    private String nClientAccount;
    private Long order;

}
