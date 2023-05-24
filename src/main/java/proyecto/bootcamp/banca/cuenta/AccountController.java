package proyecto.bootcamp.banca.cuenta;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import proyecto.bootcamp.banca.cuenta.dto.InputAccountClientDTO;
import proyecto.bootcamp.banca.cuenta.model.Client;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;
import proyecto.bootcamp.banca.cuenta.services.AccountService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/clientaccounts")
public class AccountController {
    @Autowired
    private  AccountService accountService;
    @GetMapping()
    public Flowable<ClientAccount> fetchAllClientAccount(){
        return accountService.getAllClientAccount();
    }
    @GetMapping("/client/ndoc/{nDoc}")
    public Flowable<ClientAccount> getAllClientAccountbyClientDoc(@PathVariable("nDoc") String nDoc){

        return accountService.getAllClientAccountByDoc(nDoc);
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
    @PostMapping("/create")
    public Maybe<ClientAccount> saveClientAccount(@RequestBody InputAccountClientDTO inputAccountClientDTO){
        return accountService.createClientAccount(inputAccountClientDTO);
    }

}
