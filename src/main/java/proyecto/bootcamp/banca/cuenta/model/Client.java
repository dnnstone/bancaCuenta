package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Client {
    @Id
    private String id;
    private String nDoc;
    private String tDoc;
    private String names;
    private ClientType typeClient;

    public Client(String nDoc, String tDoc, String names, ClientType typeClient) {
        this.nDoc = nDoc;
        this.tDoc = tDoc;
        this.names = names;
        this.typeClient = typeClient;
    }
}
