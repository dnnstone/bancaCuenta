package proyecto.bootcamp.banca.cuenta;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.core.Single;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.bootcamp.banca.cuenta.dto.InputAccountClientDTO;
import proyecto.bootcamp.banca.cuenta.dto.InputBankTransferDTO;
import proyecto.bootcamp.banca.cuenta.dto.ReportComissionDTO;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;
import proyecto.bootcamp.banca.cuenta.services.AccountService;


@RestController
@RequestMapping("api/v1/clientaccounts")
public class AccountController {
    @Autowired
    private  AccountService accountService;

    @GetMapping()
    public Single<ResponseEntity<Flowable<ClientAccount>>> fetchAllClientAccount(){

        return Single.just(ResponseEntity.ok().body(accountService.getAllClientAccount()));
    }
    @GetMapping("/client/ndoc/{nDoc}")
    public Single<ResponseEntity<Flowable<ClientAccount>>> getAllClientAccountbyClientDoc(@PathVariable("nDoc") String nDoc){

        return Single.just(ResponseEntity.ok().body(accountService.getAllClientAccountByDoc(nDoc)));
    }

    @GetMapping("/{nroCuenta}")
    public Single<ResponseEntity<ClientAccount>> fetchClientAccount(@PathVariable("nroCuenta") String nroCuenta){

        return accountService.getClientAccount(nroCuenta).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/id={idCuenta}")
    public Single<ResponseEntity<ClientAccount>> fetchClientAccountbyId(@PathVariable("idCuenta") String idCuenta){

        return accountService.getClientAccountbyId(idCuenta).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/deposit")
    public Single<ResponseEntity<ClientAccount>>  depositClientAccount( @RequestParam("nroCuenta") String nroCuenta ,
                                                       @RequestParam("monto") Double amount){
        return accountService.addReactiveDeposit(nroCuenta, amount).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/withdrawal")
    public Single<ResponseEntity<ClientAccount>>  withdrawalClientAccount(@RequestParam("nroCuenta") String nroCuenta ,
                                                         @RequestParam("monto") Double amount){
        return accountService.addReactiveWithdrawal(nroCuenta, amount).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/create")
    public Single<ResponseEntity<ClientAccount>> saveClientAccount(@RequestBody InputAccountClientDTO inputAccountClientDTO){
        return accountService.createClientAccount(inputAccountClientDTO).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @GetMapping(value = "/comission/{tipoProducto}")
    public Single<ResponseEntity<ReportComissionDTO>> getReportByDoc(@PathVariable("tipoProducto") String tipoProducto){
        return accountService.getReportComissionByProducto(tipoProducto).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    // now we're going to put our effort to do transference between accounts
    @PostMapping("/transfer")
    public Single<ResponseEntity<ClientAccount>> bankTransfer(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        return accountService.getAccountWithTransfer(inputBankTransferDTO).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/istransferible")
    public ResponseEntity<Single <ClientAccount>> isBankTransferible(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        Single <ClientAccount> retorno= accountService.ifGetAccountWithTransfer(inputBankTransferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
    }
}
