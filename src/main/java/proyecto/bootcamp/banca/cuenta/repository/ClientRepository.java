package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import proyecto.bootcamp.banca.cuenta.model.Client;
@Repository
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
}
