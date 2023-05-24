package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import proyecto.bootcamp.banca.cuenta.model.ClientType;
@Repository
public interface ClientTypeRepository
        extends ReactiveMongoRepository<ClientType,String> {

}
