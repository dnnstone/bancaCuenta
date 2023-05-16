package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class ClientAccount {

    @Id
    private String id;
    @Indexed(unique = true)
    private String nAccount;
    private AccountType accountType;
    private Client client;
    private List<Movement> movements;
    private Double saldo;

    public ClientAccount(String nAccount, AccountType accountType, Client client, List<Movement> movements, Double saldo) {
        this.nAccount = nAccount;
        this.accountType = accountType;
        this.client = client;
        this.movements = movements;
        this.saldo = saldo;
    }

}
