
package acme.entities.aircrafts;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Optional<Aircraft> findAircraftByRegistrationNumber(String registrationNumber);

}
