package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import proyecto.bootcamp.banca.cuenta.model.ClientAccount;

public interface ClientAccountRepository extends ReactiveMongoRepository<ClientAccount,String> {
}
