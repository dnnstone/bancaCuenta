package proyecto.bootcamp.banca.cuenta.services;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import proyecto.bootcamp.banca.cuenta.dto.*;
import proyecto.bootcamp.banca.cuenta.model.DebitCard;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;

public interface AccountService {
    public Maybe<ClientAccount> getClientAccount(String nroAccount);
    public Maybe<ClientAccount> addReactiveDeposit(String nroAccount, Double amount);
    public Maybe<ClientAccount> addReactiveWithdrawal(String nroAccount, Double amount);
    public Maybe<ClientAccount> getClientAccountbyId(String idAccount);
    public Flowable<ClientAccount> getAllClientAccount();
    public Flowable<ClientAccount> getAllClientAccountByDoc(String nDoc);
    public Maybe<ClientAccount> createClientAccount(InputAccountClientDTO inputAccountClientDTO);
    public Maybe<ReportComissionDTO> getReportComissionByProducto(String tipoProducto);
    public Maybe<ClientAccount> getAccountWithTransfer(InputBankTransferDTO inputBankTransferDTO);
    public Single<ClientAccount> ifGetAccountWithTransfer(InputBankTransferDTO inputBankTransferDTO);
    public Maybe<OutputDebitCar> addDebitCard(String nClientAccount);
    public Maybe<ClientAccount> debitDebitCard(InputdebitDebitCardDTO inputdebitDebitCardDTO);
}
