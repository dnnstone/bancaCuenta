package proyecto.bootcamp.banca.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@AllArgsConstructor
public class ClientAccount {

    @Id
    private String id;
    @Indexed(unique = true)
    private String nAccount;
    private AccountType accountType;
    private Client client;
    private List<Movement> movements;
    private Double saldo;

    private Boolean hasCard;

    private Long nOrder;
    private String nCard;

    public ClientAccount(String nAccount, AccountType accountType, Client client, List<Movement> movements, Double saldo) {
        this.nAccount = nAccount;
        this.accountType = accountType;
        this.client = client;
        this.movements = movements;
        this.saldo = saldo;
        this.hasCard =false;
        this.nOrder =0L;
        this.nCard="";
    }
    public ClientAccount() {
        this.hasCard =false;
        this.nOrder =0L;
        this.nCard="";
    }
}
