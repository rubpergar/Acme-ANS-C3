
package acme.features.authenticated.administrator.airport;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;

@Repository
public interface AdministratorAirportRepository extends AbstractRepository {

	@Query("SELECT a FROM Airport a WHERE a.id = :airporId")
	Airport getAirportById(@Param("airporId") int airporId);

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();

}
