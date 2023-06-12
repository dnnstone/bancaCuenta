package proyecto.bootcamp.banca.cuenta.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import proyecto.bootcamp.banca.cuenta.model.DebitCard;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard,String> {
    Mono<DebitCard> findDebitCardByCardNumber(String cardNumber);
}
