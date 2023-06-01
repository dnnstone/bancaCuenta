package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection ="accountType")
public class AccountType {
    @Id
    private String id;
    private String name;
    private Conditions conditions;

    public AccountType(String name, Conditions conditions) {
        this.name = name;
        this.conditions = conditions;
    }
}
