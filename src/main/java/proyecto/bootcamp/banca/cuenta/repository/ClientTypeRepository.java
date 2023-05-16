package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proyecto.bootcamp.banca.cuenta.model.ClientType;

public interface ClientTypeRepository
        extends MongoRepository<ClientType,String> {

}
