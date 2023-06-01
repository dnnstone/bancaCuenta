package proyecto.bootcamp.banca.cuenta;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.bootcamp.banca.cuenta.dto.InputAccountClientDTO;
import proyecto.bootcamp.banca.cuenta.dto.InputBankTransferDTO;
import proyecto.bootcamp.banca.cuenta.dto.ReportComissionDTO;
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
    @GetMapping(value = "/comission/{tipoProducto}")
    public Single<ReportComissionDTO> getReportByDoc(@PathVariable("tipoProducto") String tipoProducto){
        return accountService.getReportComissionByProducto(tipoProducto);
    }
    // now we're going to put our effort to do transference between accounts
    @PostMapping("/transfer")
    public Maybe <ClientAccount> bankTransfer(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        return accountService.getAccountWithTransfer(inputBankTransferDTO);
    }
    @PostMapping("/istransferible")
    public ResponseEntity<Single <ClientAccount>> isBankTransferible(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        Single <ClientAccount> retorno= accountService.ifGetAccountWithTransfer(inputBankTransferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
    }
}
