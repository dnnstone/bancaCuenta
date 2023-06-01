package proyecto.bootcamp.banca.cuenta.dto;

import lombok.Data;
import proyecto.bootcamp.banca.cuenta.model.Client;
import proyecto.bootcamp.banca.cuenta.model.Movement;


import java.util.List;

@Data
public class ClientCreditDTO {

    private String id;
    private String nCredit;
    private Client client;
    private CardCreditDTO carClient;
    private List<Movement> movements;
    private Double limitCredit;
}
