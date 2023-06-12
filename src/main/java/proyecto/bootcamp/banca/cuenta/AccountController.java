package proyecto.bootcamp.banca.cuenta;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.bootcamp.banca.cuenta.dto.*;
import proyecto.bootcamp.banca.cuenta.model.DebitCard;
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
    // transference between accounts
    @PostMapping("/transfer")
    public Single<ResponseEntity<ClientAccount>> bankTransfer(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        return accountService.getAccountWithTransfer(inputBankTransferDTO).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PostMapping("/istransferible")
    public ResponseEntity<Single <ClientAccount>> isBankTransferible(@RequestBody InputBankTransferDTO inputBankTransferDTO){
        Single <ClientAccount> retorno= accountService.ifGetAccountWithTransfer(inputBankTransferDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(retorno);
    }

    // funciona solo con un nro de cuenta como entrada, si no tiene una tarjeta, se verifica al cliente si es que tiene alguna tarjeta
    // si ya tiene una tarjeta de debito y la cuenta no esta asociada a esta, se asocia y se retorna esta tarjeta de debito
    // con el orden q tendrá el nro de cuenta mencionado. actualizando las cantidades de cuentas que estan asociadas a la debitoCard
    // si tiene una tarjeta asociada, simplemente se retorna esta.
    @PostMapping("/createCard")
    public Single<ResponseEntity<OutputDebitCar>> addNewDebitCard(@RequestParam("nClientAccount") String nClientAccount){
        return accountService.addDebitCard(nClientAccount).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //pagar y retirar desde DebitCar
    @PostMapping("debitCar")
    public Single<ResponseEntity<ClientAccount>> debitDebitCard(@RequestBody InputdebitDebitCardDTO inputdebitDebitCardDTO){
        return accountService.debitDebitCard(inputdebitDebitCardDTO).map(s->ResponseEntity.ok().body(s)).defaultIfEmpty(ResponseEntity.notFound().build());

    }

}
