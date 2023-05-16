package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proyecto.bootcamp.banca.cuenta.model.AccountType;

public interface AccountTypeRepository extends MongoRepository<AccountType,String> {
}
