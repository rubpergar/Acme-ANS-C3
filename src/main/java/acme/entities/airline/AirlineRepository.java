
package acme.entities.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineRepository extends AbstractRepository {

	@Query("SELECT a.codeIATA FROM Airline a")
	public Collection<String> airlineCodesIATAs();

	@Query("SELECT a FROM Airline a WHERE a.codeIATA = :IATACode")
	public Collection<Airline> getAirlinesByIATA(@Param("IATACode") String IATACode);

}
