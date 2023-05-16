package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;

public interface ClientAccountRepository extends MongoRepository<ClientAccount,String> {
}
