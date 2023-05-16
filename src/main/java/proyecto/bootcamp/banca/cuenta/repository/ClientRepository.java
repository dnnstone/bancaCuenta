package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proyecto.bootcamp.banca.cuenta.model.Client;

public interface ClientRepository extends MongoRepository <Client, String>{
}
