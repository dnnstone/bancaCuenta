package proyecto.bootcamp.banca.cuenta;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import proyecto.bootcamp.banca.cuenta.model.Client;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;
import proyecto.bootcamp.banca.cuenta.services.AccountService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/clientaccounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @GetMapping()
    public Flowable<ClientAccount> fetchAllClientAccount(){
        return accountService.getAllClientAccount();
    }

    @GetMapping("/{nroCuenta}")
    public Maybe<ClientAccount> fetchClientAccount(@PathVariable("nroCuenta") String nroCuenta){

        return accountService.getClientAccount(nroCuenta);
    }

    @GetMapping("/id={idCuenta}")
    public ClientAccount fetchClientAccountbyId(@PathVariable("idCuenta") String idCuenta){

        return accountService.getClientAccountbyId(idCuenta);
    }

    @PostMapping("/deposit")
    public Maybe<ClientAccount>  depositClientAccount( @RequestParam("nroCuenta") String nroCuenta ,
                                        @RequestParam("monto") Double amount){
        return accountService.addReactiveDeposit(nroCuenta, amount);
    }
    @PostMapping("/withdrawal")
    public Maybe<ClientAccount>  withdrawalClientAccount(@RequestParam("nroCuenta") String nroCuenta ,
                                                         @RequestParam("monto") Double amount){
        return accountService.addReactiveWithdrawal(nroCuenta, amount);
    }
}
