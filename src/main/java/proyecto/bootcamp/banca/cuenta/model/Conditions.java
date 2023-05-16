package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;

@Data
public class Conditions {
    private Integer maintainFee;
    private Integer maxMovement;
    private Integer diaMovement;
    private String dataHeadlines;
    private String dataSigners;

    public Conditions(Integer maintainFee, Integer maxMovement, Integer diaMovement, String dataHeadlines, String dataSigners) {
        this.maintainFee = maintainFee;
        this.maxMovement = maxMovement;
        this.diaMovement = diaMovement;
        this.dataHeadlines = dataHeadlines;
        this.dataSigners = dataSigners;
    }
}
