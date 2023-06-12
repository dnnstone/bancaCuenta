package proyecto.bootcamp.banca.cuenta.dto;

import lombok.Data;

@Data
public class InputdebitDebitCardDTO {

    private String DebitCardNumber;
    private Double amount;
    private String typeDebit;
}
