package proyecto.bootcamp.banca.cuenta.services;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.stereotype.Service;
import proyecto.bootcamp.banca.cuenta.dto.InputAccountClientDTO;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;

public interface AccountService {
    public Maybe<ClientAccount> getClientAccount(String nroAccount);
    public Maybe<ClientAccount> addReactiveDeposit(String nroAccount, Double amount);
    public Maybe<ClientAccount> addReactiveWithdrawal(String nroAccount, Double amount);
    public ClientAccount getClientAccountbyId(String idAccount);
    public Flowable<ClientAccount> getAllClientAccount();
    public Flowable<ClientAccount> getAllClientAccountByDoc(String nDoc);
    public Maybe<ClientAccount> createClientAccount(InputAccountClientDTO inputAccountClientDTO);
}
