package proyecto.bootcamp.banca.cuenta.services;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proyecto.bootcamp.banca.cuenta.model.Client;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;
import proyecto.bootcamp.banca.cuenta.model.Movement;
import proyecto.bootcamp.banca.cuenta.repository.ClientAccountRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.adapter.rxjava.RxJava3Adapter;

@AllArgsConstructor
@Service
public class AccountService {
    private final ClientAccountRepository clientAccountRepository;
    private final ClientRepository clientRepository;
    @Autowired
    private final ReactiveMongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private Environment env;

    public Maybe<ClientAccount> getClientAccount(String nroAccount){
        Query query =new Query();
        query.addCriteria(Criteria.where("nAccount").is(nroAccount));
        return mongoTemplate.
                findOne(query, ClientAccount.class)
                .as(RxJava3Adapter::monoToMaybe)
                .map(client->{
                    RestTemplate restTemplate= new RestTemplate();
                    client.setClient(restTemplate.getForObject(env.getProperty("apis.cliente")+client.getClient().getId(), Client.class));
                    return client;
                });

    }

    public Maybe<ClientAccount> addReactiveDeposit(String nroAccount, Double amount){

        this.getClientAccount(nroAccount).subscribe(System.out::println);
        return this.getClientAccount(nroAccount)
                .filter(this::isAllowedMovement)
                .map(cl->{
                    cl.setSaldo(cl.getSaldo()+amount);
                    List<Movement> listMov= cl.getMovements();
                    listMov.add(new Movement("deposito",amount,new Date()));
                    cl.setMovements(listMov);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::save)
                .as(RxJava3Adapter::monoToMaybe);

    }
//  Agrega un depósito a la cuenta
    public Maybe<ClientAccount> addReactiveWithdrawal(String nroAccount, Double amount){
        return this.getClientAccount(nroAccount)
                .filter(this::isAllowedMovement)    //filtros de cantidad de movs y fecha
                .filter(s->s.getSaldo().compareTo(amount)>=0)   //filtro de saldo positivo
                .map(cl->{
                    cl.setSaldo(cl.getSaldo()-amount);
                    List<Movement> listMov= cl.getMovements();
                    listMov.add(new Movement("retiro",amount,new Date()));
                    cl.setMovements(listMov);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::save)
                .as(RxJava3Adapter::monoToMaybe);
}
    public Boolean addDeposit(String nroAccount, Double amount){
        ClientAccount clientAccount=getClientAccount(nroAccount).blockingGet();
        Supplier<Boolean> suMov=()->{
            if(this.isAllowedMovement(clientAccount)){
                Movement addMoviment= new Movement("deposito",amount,new Date());
                List<Movement> listMovements= clientAccount.getMovements()!=null?clientAccount.getMovements():new ArrayList<>();
                listMovements.add(addMoviment);
                clientAccount.setMovements(listMovements);
                clientAccount.setSaldo(clientAccount.getSaldo()+amount);
                clientAccountRepository.save(clientAccount);
                return true;
            }
            else{
                return false;
            }
        };
        return suMov.get();
    }
//Agrega un retiro a la cuenta
    public Boolean addWithdrawal(String nroAccount, Double amount){
        ClientAccount clientAccount=getClientAccount(nroAccount).blockingGet();
        BiPredicate<Double,Double> biOutdraw= (s,m)->s.compareTo(m)<0;
        Supplier<Boolean> suMovs=()->{
            if(biOutdraw.test(clientAccount.getSaldo(),amount) )
            {
                return false;
            }
            else
            {
                if(this.isAllowedMovement(clientAccount)){
                    Movement addMoviment= new Movement("retiro",amount,new Date());
                    List<Movement> listMovements= clientAccount.getMovements()!=null?clientAccount.getMovements():new ArrayList<>();
                    listMovements.add(addMoviment);
                    clientAccount.setMovements(listMovements);
                    clientAccount.setSaldo(clientAccount.getSaldo()-amount);
                    clientAccountRepository.save(clientAccount);
                    return true;
                }
                else {
                    logger.info("filtro validaciones de cuenta");
                    return false;
                }

            }
        };
        return suMovs.get();
        }

        // Método Privado que verifica la configuración de la cuenta y detecta si es permitido
        // un movimiento adicional
    private Boolean isAllowedMovement(ClientAccount clientAccount){
        Date today= new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(today);
        Integer countMovsApply= (int)clientAccount.getMovements().stream()
                .filter(m->{
                            Integer month= today.getMonth();
                            Integer year= today.getYear();
                            return m.getDate().getMonth()==month && m.getDate().getYear()==year;
                        }).count();

          //      .collect(Collectors.toList()).size();

        BiPredicate<Integer,Integer> maxMovements= (confAccount,clientMovs)->confAccount.equals(-1)||confAccount>clientMovs;
        Predicate<Integer> dayAllowed=confAccount->confAccount.equals(0)||confAccount.equals(calendar.get(Calendar.DAY_OF_MONTH));


        logger.info("Dnns: Movimientos del mes: "+countMovsApply);
        logger.info("Dnns: Dia configurado: "+clientAccount.getAccountType().getConditions().getDiaMovement()+" Día hoy: "+calendar.get(Calendar.DAY_OF_MONTH));
        return maxMovements.test(clientAccount.getAccountType().getConditions().getMaxMovement(),countMovsApply)
                && dayAllowed.test(clientAccount.getAccountType().getConditions().getDiaMovement());

    }
    public ClientAccount getClientAccountbyId(String idAccount){
        return clientAccountRepository.findById(idAccount).as(RxJava3Adapter::monoToMaybe).blockingGet();
    }
    public Flowable<ClientAccount> getAllClientAccount(){
        return clientAccountRepository.findAll()
                .as(RxJava3Adapter::fluxToFlowable);
    }
    public Flowable<ClientAccount> getAllClientAccountByDoc(String nDoc){
//        Query query =new Query();
//        query.addCriteria(Criteria.where("client.nDoc").is(nDoc));
//        return mongoTemplate
//                .find(query, ClientAccount.class)
//                .as(RxJava3Adapter::fluxToFlowable);

        return clientAccountRepository.findAll().filter(s->s.getClient().getNDoc().equals(nDoc))
                .as(RxJava3Adapter::fluxToFlowable);
    }
}
