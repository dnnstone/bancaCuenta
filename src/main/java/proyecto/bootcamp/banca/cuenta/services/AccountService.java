package proyecto.bootcamp.banca.cuenta.services;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.stereotype.Service;
import proyecto.bootcamp.banca.cuenta.model.Client;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;
import proyecto.bootcamp.banca.cuenta.model.Movement;
import proyecto.bootcamp.banca.cuenta.repository.ClientAccountRepository;
import proyecto.bootcamp.banca.cuenta.repository.ClientRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@AllArgsConstructor
@Service
public class AccountService {
    private final ClientAccountRepository clientAccountRepository;
    private final ClientRepository clientRepository;
    private final MongoTemplate mongoTemplate;


    public List<ClientAccount> getAllClientAccount(){
        return clientAccountRepository.findAll();
    }
    public List<Client> getAllClients()
    {
        return clientRepository.findAll();
    }
    public ClientAccount getClientAccount(String nroAccount){
        Query query =new Query();
        query.addCriteria(Criteria.where("nAccount").is(nroAccount));
        return mongoTemplate.findOne(query, ClientAccount.class);
//        return clientAccountRepository.findOne();
    }
    public ClientAccount getClientAccountbyId(String idAccount){
        return clientAccountRepository.findById(idAccount).get();
    }

    public Boolean addDeposit(String nroAccount, Double amount){
        ClientAccount clientAccount=getClientAccount(nroAccount);
        LocalDate localDate;
        Date today=new Date();
//        Boolean apply= clientAccount.getMovements().stream().filter(m->{
//
//            Integer month= today.getMonth();
//            Integer year= today.getYear();
//            return m.getDate().getMonth()==month && m.getDate().getYear()==year;
////            Date firstDay= new Date();
////
////            firstDay.setHours(0);
////            firstDay.setMinutes(0);
////            firstDay.setSeconds(0);
////            firstDay.(0);
////            firstDay.setMonth(m.getDate().getMonth());
////            firstDay.setYear(m.getDate().getYear());
//
//        })


        if(this.isAllowedMovement(clientAccount)){
            Movement increaseMoviment= new Movement("deposito",amount,new Date());
            List<Movement> listMovements= clientAccount.getMovements()!=null?clientAccount.getMovements():new ArrayList<>();
            listMovements.add(increaseMoviment);
            clientAccount.setMovements(listMovements);
            clientAccount.setSaldo(clientAccount.getSaldo()+amount);
            clientAccountRepository.save(clientAccount);
            return true;
        }
        else{
            return false;
        }

    }

    public Boolean addWithdrawal(String nroAccount, Double amount){
        ClientAccount clientAccount=getClientAccount(nroAccount);
        Movement increaseMoviment= new Movement("retiro",amount,new Date());
        BiPredicate<Double,Double> biOutdraw= (s,m)->s.compareTo(m)<0;
        Supplier<Boolean> suMovs=()->{
            if(biOutdraw.test(clientAccount.getSaldo(),amount) )
            {
                System.out.println("suMovs if");
                return false;
            }
            else
            {
                if(this.isAllowedMovement(clientAccount)){
                    System.out.println("suMovs else");
                    List<Movement> listMovements= clientAccount.getMovements()!=null?clientAccount.getMovements():new ArrayList<>();
                    listMovements.add(increaseMoviment);
                    clientAccount.setMovements(listMovements);
                    clientAccount.setSaldo(clientAccount.getSaldo()-amount);
                    clientAccountRepository.save(clientAccount);
                    return true;
                }
                else {
                    System.out.println("filtro validaciones");
                    return false;
                }

            }
        };
        return suMovs.get();
        }

    private Boolean isAllowedMovement(ClientAccount clientAccount){
        Date today= new Date();
        BiPredicate<Integer,Integer> maxMovements= (confAccount,clientMovs)->confAccount.equals(-1)||confAccount>clientMovs;

        Predicate<Integer> dayAllowed=confAccount->confAccount.equals(0)||confAccount.equals(today.getDay());

        return maxMovements.test(clientAccount.getAccountType().getConditions().getMaxMovement(),clientAccount.getMovements()!=null?clientAccount.getMovements().size():0)
                && dayAllowed.test(clientAccount.getAccountType().getConditions().getDiaMovement());

    }

}
