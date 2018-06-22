
package niuka.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "shared", path = "shared")
public interface SharedRepository extends MongoRepository<Shared, String> {
	List<Shared> findByUserId(@Param("userId") String userId);
}
