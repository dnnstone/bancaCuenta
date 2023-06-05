package proyecto.bootcamp.banca.cuenta.services;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import proyecto.bootcamp.banca.cuenta.dto.*;
import proyecto.bootcamp.banca.cuenta.model.*;
import proyecto.bootcamp.banca.cuenta.repository.AccountTypeRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientAccountRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientRepository;
import proyecto.bootcamp.banca.cuenta.utils.ClientAccountUtils;
import reactor.adapter.rxjava.RxJava3Adapter;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class AccountServiceImp implements AccountService{
    @Autowired
    private  ClientAccountRepository clientAccountRepository;
    @Autowired
    private  ClientRepository clientRepository;
    @Autowired
    private  AccountTypeRepository accountTypeRepository;
    @Autowired
    private  ReactiveMongoTemplate mongoTemplate;
    @Autowired
    private static  Logger logger = LoggerFactory.getLogger(AccountService.class);
    @Autowired
    private Environment env;
    @Override
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
    @Override
    public Maybe<ClientAccount> addReactiveDeposit(String nroAccount, Double amount){

        this.getClientAccount(nroAccount).subscribe(System.out::println);
        return this.getClientAccount(nroAccount)
//                .filter(this::isAllowedMovement)
                .filter(cli->isAllowedMovementBySaldo(cli,amount,"deposito"))
                .map(cl->{
                    cl.setSaldo(cl.getSaldo()+amount);
                    List<Movement> listMov= cl.getMovements();
                    listMov.add(new Movement("deposito",amount,new Date()));
                    if(!isAllowedMovement(cl)){
                        listMov.add(new Movement("comision",cl.getAccountType().getConditions().getChargeOfTransaction(),new Date()));
                        cl.setSaldo(cl.getSaldo()-cl.getAccountType().getConditions().getChargeOfTransaction());}
                    cl.setMovements(listMov);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::save)
                .as(RxJava3Adapter::monoToMaybe);

    }
    //  Agrega un depósito a la cuenta
    @Override
    public Maybe<ClientAccount> addReactiveWithdrawal(String nroAccount, Double amount){
        return this.getClientAccount(nroAccount)
//                .filter(this::isAllowedMovement)    //filtros de cantidad de movs y fecha
                .filter(cli->isAllowedMovementBySaldo(cli,amount,"retiro"))
                .filter(s->s.getSaldo().compareTo(amount)>=0)   //filtro de saldo positivo
                .map(cl->{
                    cl.setSaldo(cl.getSaldo()-amount);
                    List<Movement> listMov= cl.getMovements();
                    listMov.add(new Movement("retiro",amount,new Date()));
                    if(!isAllowedMovement(cl)){
                        listMov.add(new Movement("comision",cl.getAccountType().getConditions().getChargeOfTransaction(),new Date()));
                        cl.setSaldo(cl.getSaldo()-cl.getAccountType().getConditions().getChargeOfTransaction());}
                    cl.setMovements(listMov);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::save)
                .as(RxJava3Adapter::monoToMaybe);
    }

    // Método Privado que verifica la configuración de la cuenta y detecta si es permitido
    // un movimiento adicional
    @Override
    public Maybe<ClientAccount> getClientAccountbyId(String idAccount){
        return clientAccountRepository.findById(idAccount).as(RxJava3Adapter::monoToMaybe);
    }
    @Override
    public Flowable<ClientAccount> getAllClientAccount(){
        return clientAccountRepository.findAll()
                .as(RxJava3Adapter::fluxToFlowable);
    }
    @Override
    public Flowable<ClientAccount> getAllClientAccountByDoc(String nDoc){
        Query query =new Query();
        query.addCriteria(Criteria.where("client.nDoc").is(nDoc));
        return mongoTemplate
                .find(query, ClientAccount.class)
                .as(RxJava3Adapter::fluxToFlowable);

//        return clientAccountRepository.findAll().filter(s->s.getClient().getNDoc().equals(nDoc))
//                .as(RxJava3Adapter::fluxToFlowable);
    }
    @Override
    public Maybe<ClientAccount> createClientAccount(InputAccountClientDTO inputAccountClientDTO){
        return Maybe.just(inputAccountClientDTO)
                .filter(input->this.isAllowedToNewAccount(clientRepository.findById(input.getClientId()).block()
                        ,accountTypeRepository.findById(input.getTypeAccountId()).block()
                        ,input.getInitialAmount()
                        ))
                .map(s->{
                    List<Movement> movements= new ArrayList<>();
                         if(!s.getInitialAmount().equals(0.0))
                        {
                            movements.add(new Movement("initial",s.getInitialAmount(),new Date()));
                        }
                        return new ClientAccount("0033-"+ ClientAccountUtils.createNumber()
                                ,accountTypeRepository.findById(s.getTypeAccountId()).block()
                                ,clientRepository.findById(s.getClientId()).block()
                                ,movements
                                ,s.getInitialAmount());
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::insert).as(RxJava3Adapter::monoToMaybe);
    }

    private Boolean isAllowedMovement(ClientAccount clientAccount){
        Date today= new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(today);
        Integer countMovsApply= (int)clientAccount.getMovements().stream()
                .filter(m->{
                    Integer month= today.getMonth();
                    Integer year= today.getYear();
                    return m.getDate().getMonth()==month && m.getDate().getYear()==year;
                })
                .filter(m->m.getType().equals("deposito")||m.getType().equals("retiro"))
                .count();

        //      .collect(Collectors.toList()).size();

        BiPredicate<Integer,Integer> maxMovements= (confAccount,clientMovs)->confAccount.equals(-1)||confAccount>=clientMovs;
        Predicate<Integer> dayAllowed= confAccount->confAccount.equals(0)||confAccount.equals(calendar.get(Calendar.DAY_OF_MONTH));


        logger.info("Dnns: Movimientos del mes: "+countMovsApply);
        logger.info("Dnns: Dia configurado: "+clientAccount.getAccountType().getConditions().getDiaMovement()+" Día hoy: "+calendar.get(Calendar.DAY_OF_MONTH));

        System.out.println("retorno de isAllowed: "+maxMovements.test(clientAccount.getAccountType().getConditions().getMaxMovement(),countMovsApply)
                +" and "+ dayAllowed.test(clientAccount.getAccountType().getConditions().getDiaMovement()));
        return maxMovements.test(clientAccount.getAccountType().getConditions().getMaxMovement(),countMovsApply)
                && dayAllowed.test(clientAccount.getAccountType().getConditions().getDiaMovement());

    }
    private Boolean existAccount(String nroAccount){
        Query query =new Query();
        query.addCriteria(Criteria.where("nAccount").is(nroAccount));
        return mongoTemplate.
                exists(query, ClientAccount.class)
                .as(RxJava3Adapter::monoToSingle)
                .blockingGet();
    }

    private Boolean isAllowedToNewAccount(Client clientTemp,AccountType accountTypeTemp, Double amountToCheck){

        RestTemplate restTemplate= new RestTemplate();
        //-1 significa ilimitado
        Long nAccountsLikeCreate= this.getAllClientAccountByDoc(clientTemp.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals(accountTypeTemp.getName()))
                .count().blockingGet(); //cuentas de cliente que son como las que se quiere crear
        Long nAllAccountsClient= this.getAllClientAccountByDoc(clientTemp.getNDoc())
                .count().blockingGet();
        Long nCcAccountsClient= this.getAllClientAccountByDoc(clientTemp.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Corriente"))
                .count().blockingGet();
        Long nCaAccountsClient= this.getAllClientAccountByDoc(clientTemp.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Ahorros"))
                .count().blockingGet();
        Long nCpfAccountsClient= this.getAllClientAccountByDoc(clientTemp.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Plazo Fijo"))
                .count().blockingGet();

        Integer dato= accountTypeTemp.getName().equals("Corriente")?clientTemp.getTypeClient().getAccountConditions().getNMaxCCorriente():
                (accountTypeTemp.getName().equals("Ahorros")?clientTemp.getTypeClient().getAccountConditions().getNMaxCAhorro():
                        (accountTypeTemp.getName().equals("Plazo Fijo")?clientTemp.getTypeClient().getAccountConditions().getNMaxCPFijo():-1));

        System.out.println("totale cuenta: "+nAllAccountsClient+" permitidos: "+clientTemp.getTypeClient().getAccountConditions().getNMaxCuentas()+"\n"+
                " cuentas del tipo que se quiere crear: "+nAccountsLikeCreate+" "+accountTypeTemp.getName()+"son: "+dato+"\n"+
                " cuentas del tipo Corriente: "+nCcAccountsClient+" permitidos: "+clientTemp.getTypeClient().getAccountConditions().getNMaxCCorriente()+"\n"+
                " cuentas del tipo Ahorros: "+nCaAccountsClient+" permitidos: "+clientTemp.getTypeClient().getAccountConditions().getNMaxCAhorro()+"\n"+
                " cuentas del tipo Plazo Fijo: "+nCpfAccountsClient+" permitidos: "+clientTemp.getTypeClient().getAccountConditions().getNMaxCPFijo()+"\n"+
                "is OrCCCPF:"+clientTemp.getTypeClient().getAccountConditions().getOrCCCPF()
        );



        Predicate<Client> allowTotalAccount= client->client.getTypeClient().getAccountConditions().getNMaxCuentas().equals(-1)||
                this.getAllClientAccountByDoc(client.getNDoc()).count().blockingGet().compareTo(client.getTypeClient().getAccountConditions().getNMaxCuentas().longValue())<0;

        Predicate<Client> allowTypeAccount= client->dato.equals(-1)||
                this.getAllClientAccountByDoc(client.getNDoc())
                        .filter(cliAcc->cliAcc.getAccountType().getName().equals(accountTypeTemp.getName()))
                        .count().blockingGet().compareTo(dato.longValue())<0;

        Predicate<Client>  allowOrCCCPF= client->(this.getAllClientAccountByDoc(client.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Corriente")).count().blockingGet()+this.getAllClientAccountByDoc(client.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Plazo Fijo")).count().blockingGet())<=1 ||
                !clientTemp.getTypeClient().getAccountConditions().getOrCCCPF();
        Predicate<Client> allowWithCurrentAccount= client-> this.getAllClientAccountByDoc(client.getNDoc())
                .filter(cliAcc->cliAcc.getAccountType().getName().equals("Corriente")).count().blockingGet().equals(1)|| !accountTypeTemp.getConditions().getWithCurrentAccount();

        String url=env.getProperty("apis.credit")+"client/ndoc/"+clientTemp.getNDoc();
        logger.info("url credit: "+url);


        Predicate<AccountType> allowCreditCar= accountType -> !accountType.getConditions().getWithCreditCar()|| restTemplate
                .getForObject(url,ClientCreditDTO[].class).length>=1;


        Predicate<AccountType> allowWithMinimalAmount= accountType->amountToCheck>=accountType.getConditions().getMinAmount();
        logger.info("retorno is isAllowedToNewAccount: \n total "+            allowTotalAccount.test(clientTemp)
                +" \nand max por tipo de cuenta"+allowTypeAccount.test(clientTemp)
                +" \nand CC o PF"+allowOrCCCPF.test(clientTemp)
                +" \nand con cuenta corriente"+allowWithCurrentAccount.test(clientTemp)
                +" \nand minimo monto "+allowWithMinimalAmount.test(accountTypeTemp)
                +" \nand credit car "+allowCreditCar.test(accountTypeTemp)
        );
        return
            allowTotalAccount.test(clientTemp)
            &&allowTypeAccount.test(clientTemp)
            &&allowOrCCCPF.test(clientTemp)
            &&allowWithCurrentAccount.test(clientTemp)
            &&allowWithMinimalAmount.test(accountTypeTemp)
            &&allowCreditCar.test(accountTypeTemp)
            ;
    }
    private Boolean isAllowedMovementBySaldo(ClientAccount clientAccount,Double saldo,String tipoTransaction){
        Date today= new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(today);
        Integer countMovsApply= (int)clientAccount.getMovements().stream()
                .filter(m->{
                    Integer month= today.getMonth();
                    Integer year= today.getYear();
                    return m.getDate().getMonth()==month && m.getDate().getYear()==year;
                })
                .filter(m->m.getType().equals("deposito")||m.getType().equals("retiro"))
                .count();

        if(tipoTransaction.equals("deposito")){
            if(countMovsApply>clientAccount.getAccountType().getConditions().getMaxMovement()
                    && (clientAccount.getSaldo()+saldo<=clientAccount.getAccountType().getConditions().getChargeOfTransaction())){
                return false;
            }
            else{return true;}
        }else if(tipoTransaction.equals("retiro")){// retiro
            if(countMovsApply>clientAccount.getAccountType().getConditions().getMaxMovement()
                    && (clientAccount.getSaldo()<clientAccount.getAccountType().getConditions().getChargeOfTransaction()+saldo)){
                return false;
            }
            else{return true;}
        }
        else{ return false;}
    }

    public Maybe<ReportComissionDTO>  getReportComissionByProducto(String tipoProducto){
        Query query =new Query();
        query.addCriteria(Criteria.where("name").is(tipoProducto));
        return  mongoTemplate.findOne(query, AccountType.class)
                .map(s->{
                    Query query1=new Query();
                    query1.addCriteria(Criteria.where("accountType.id").is(s.getId()));
                    return  new ReportComissionDTO(
                            s.getName()
                            ,mongoTemplate.find(query1,ClientAccount.class).as(RxJava3Adapter::fluxToFlowable).count().blockingGet()
                            ,mongoTemplate.find(query1,ClientAccount.class).as(RxJava3Adapter::fluxToFlowable)
                                    .map(s1->s1.getMovements())
                                    .flatMapStream(t->t.stream())
                                    .filter(s2->s2.getType().equals("comision"))
                                    .count().blockingGet()
                            ,mongoTemplate.find(query1,ClientAccount.class).as(RxJava3Adapter::fluxToFlowable)
                                    .map(s1->s1.getMovements())
                                    .flatMapStream(t->t.stream())
                                    .filter(s2->s2.getType().equals("comision"))
                                    .map(s3->s3.getAmount())
                                    .reduce((x,y)->x+y).blockingGet()
                            ,mongoTemplate.find(query1,ClientAccount.class).as(RxJava3Adapter::fluxToFlowable)
                                    .map(s1->s1.getMovements())
                                    .flatMapStream(t->t.stream())
                                    .filter(s2->s2.getType().equals("comision"))
                                    .map(a->Flowable.just(new TransactionDTO(
                                                a.getDate()
                                                ,a.getType()
                                                , a.getAmount()
                                                ,Single.just(a)
                                                    .map(b-> {
                                                        Query query3 = new Query();
                                                        query3.addCriteria(Criteria.where("movements.date").is(b.getDate()));
                                                        return mongoTemplate.findOne(query3, ClientAccount.class)
                                                                .as(RxJava3Adapter::monoToSingle)
                                                                .map(s5 -> s5.getNAccount()).blockingGet();
                                                    })
                                                    .blockingGet()
                                                    ,Single.just(a).map(b->{
                                                            Query query3=new Query();
                                                            query3.addCriteria(Criteria.where("movements.date").is(b.getDate()));
                                                            System.out.println(b.getDate());
                                                            return mongoTemplate.findOne(query3,ClientAccount.class)
                                                                    .as(RxJava3Adapter::monoToSingle)
                                                                    .map(s1->s1.getMovements())
                                                                    .blockingGet()
                                                                    .stream()
                                                                    .filter(s2->(s2.getType().equals("retiro")||
                                                                            s2.getType().equals("deposito"))&&
                                                                            s2.getDate().equals(b.getDate()))
                                                                    .map(s4->s4.getType())
                                                                    .collect(Collectors.joining());

                                                    })
                                                    .blockingGet()

                                                    ,Single.just(a).map(b->{
                                                        Query query3=new Query();
                                                        query3.addCriteria(Criteria.where("movements.date").is(b.getDate()));
                                                        System.out.println(b.getDate());
                                                        return mongoTemplate.findOne(query3,ClientAccount.class)
                                                                .as(RxJava3Adapter::monoToSingle)
                                                                .map(s1->s1.getMovements())
                                                                .blockingGet()
                                                                .stream()
                                                                .filter(s2->(s2.getType().equals("retiro")||
                                                                        s2.getType().equals("deposito"))&&
                                                                        s2.getDate().equals(b.getDate()))
                                                                .map(s4->s4.getAmount())
                                                                .reduce(Double.MIN_VALUE,Double::sum)

                                                                ;
                                                    })
                                                    .blockingGet()
                                    )))
                                    .flatMapStream(t2->t2.blockingStream())
                                    .blockingStream().collect(Collectors.toList())

                    )  ;
                })
                .as(RxJava3Adapter::monoToMaybe);

    }
    public Maybe<ClientAccount> getAccountWithTransfer(InputBankTransferDTO inputBankTransferDTO){

        return this.getClientAccount(inputBankTransferDTO.getOriginAccount())
                .filter(cli->cli.getSaldo()>= inputBankTransferDTO.getAmount()&& this.existAccount(inputBankTransferDTO.getDestinyAccount()))
                .map(cliAc->{
                    cliAc.setSaldo(cliAc.getSaldo()- inputBankTransferDTO.getAmount());
                    List<Movement> listMov= cliAc.getMovements();
                    listMov.add(new Movement("transfer origin",inputBankTransferDTO.getAmount(),new Date()));
                    cliAc.setMovements(listMov);
                    // here we add the movemento of transfer destiny
                    this.getClientAccount(inputBankTransferDTO.getDestinyAccount())
                            .map(cliDes->{
                                cliDes.setSaldo(cliDes.getSaldo()+ inputBankTransferDTO.getAmount());
                                List<Movement> listMo=cliDes.getMovements();
                                listMo.add(new Movement("transfer destiny",inputBankTransferDTO.getAmount(),new Date()));
                                cliDes.setMovements(listMo);
                                return cliDes;

                            })
                            .to(RxJava3Adapter::maybeToMono)
                            .flatMap(clientAccountRepository::save)
                            .subscribe();
                    return cliAc;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(clientAccountRepository::save)
                .as(RxJava3Adapter::monoToMaybe);
        //we're going to generate 2 operations, one will be an  withdrawal, and the another will be a deposit

    }

    public Single<ClientAccount> ifGetAccountWithTransfer(InputBankTransferDTO inputBankTransferDTO){
//        return this.getClientAccount(inputBankTransferDTO.getOriginAccount())
//                .filter(cli->cli.getSaldo()>= inputBankTransferDTO.getAmount()&& this.existAccount(inputBankTransferDTO.getDestinyAccount()))
//                .toSingle();

        return Single.create(emitter -> {
            if(!this.getClientAccount(inputBankTransferDTO.getOriginAccount())
                    .filter(cli->cli.getSaldo()>= inputBankTransferDTO.getAmount()&& this.existAccount(inputBankTransferDTO.getDestinyAccount()))
                    .isEmpty().blockingGet())
            emitter.onSuccess(

                    this.getClientAccount(inputBankTransferDTO.getOriginAccount())
                .filter(cli->cli.getSaldo()>= inputBankTransferDTO.getAmount()&& this.existAccount(inputBankTransferDTO.getDestinyAccount()))
                .toSingle().blockingGet()
            );
            else{
                emitter.onError(new Throwable("No cumplen con las validaciones"));
            }

        });

    }
}
