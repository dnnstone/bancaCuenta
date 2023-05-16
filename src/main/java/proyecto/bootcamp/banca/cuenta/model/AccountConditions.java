package proyecto.bootcamp.banca.cuenta.model;

import lombok.Data;

@Data
public class AccountConditions {
    private Integer nMaxCuentas;
    private Integer nMaxCAhorro;
    private Integer nMaxCCorriente;
    private Integer nMaxCPFijo;
    private Boolean orCCCPF;

    public AccountConditions(Integer nMaxCuentas, Integer nMaxCAhorro, Integer nMaxCCorriente, Integer nMaxCPFijo, Boolean orCCCPF) {
        this.nMaxCuentas = nMaxCuentas;
        this.nMaxCAhorro = nMaxCAhorro;
        this.nMaxCCorriente = nMaxCCorriente;
        this.nMaxCPFijo = nMaxCPFijo;
        this.orCCCPF = orCCCPF;
    }
}
