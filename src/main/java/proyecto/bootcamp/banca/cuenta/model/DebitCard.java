package proyecto.bootcamp.banca.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
//@AllArgsConstructor
public class DebitCard {
    @Id
    private String id;
    @Indexed(unique = true)
    private String cardNumber;
    private Client client;
    private List<ClientAccount> clientAccountList;
    private ClientAccount mainAccount;

    public DebitCard(String cardNumber, Client client, List<ClientAccount> clientAccountList, ClientAccount mainAccount) {
        this.cardNumber=cardNumber;
        this.client = client;
        this.clientAccountList = clientAccountList;
        this.mainAccount = mainAccount;
    }
}
