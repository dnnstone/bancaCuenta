package proyecto.bootcamp.banca.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
@AllArgsConstructor
public class TransactionDTO {
    private Date date;
    private String operation;
    private Double amount;
    private String nAccount;
    private String OperationOfComission;
    private Double amountOfOperationOfComission;
}
