
package acme.entities.airports;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirportRepository extends AbstractRepository {

	@Query("SELECT a.codeIATA FROM Airport a")
	public Collection<String> airportCodesIATAs();

	@Query("SELECT a FROM Airport a WHERE a.codeIATA = :IATACode")
	public Collection<Airport> getAirportByIATA(@Param("IATACode") String IATACode);

	@Query("SELECT a FROM Airport a")
	public Collection<Airport> findAllAirports();

}
