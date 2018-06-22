
package niuka.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "card", path = "card")
public interface CardRepository extends MongoRepository<Card, String> {
	List<Card> findByUserId(@Param("userId") String userId);
}
