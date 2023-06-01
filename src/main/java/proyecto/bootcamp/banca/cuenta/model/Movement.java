package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Movement {
    private String type ;
    private Double amount;
    private Date date;

    public Movement(String type, Double amount, Date date) {
        this.type = type;
        this.amount = amount;
        this.date = date;
    }
}
