
package acme.entities.aircrafts;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;

public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT COUNT(a) > 0 FROM Aircraft a WHERE a.registrationNumber = :registrationNumber AND a.id <> :id")
	boolean existsByRegistrationNumberAndNotId(@Param("registrationNumber") String registrationNumber, @Param("id") Integer id);

	@Query("SELECT a FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Optional<Aircraft> findAircraftByRegistrationNumber(String registrationNumber);

}
