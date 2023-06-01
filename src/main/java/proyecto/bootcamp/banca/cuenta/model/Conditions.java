package proyecto.bootcamp.banca.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Conditions {
    private Integer maintainFee;
    private Integer maxMovement;
    private Integer diaMovement;
    private String dataHeadlines;
    private String dataSigners;
    private Double minAmount;
    private Boolean withCreditCar;
    private Boolean withCurrentAccount;
    private Double chargeOfTransaction;

}
