package proyecto.bootcamp.banca.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportComissionDTO {
    private String producto;
    private Long countAccounts;
    private Long countTransactions;
    private Double amountTotalComission;
    List<TransactionDTO> transactionList;

}
