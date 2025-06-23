
package acme.features.any.flight.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface AnyLegRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l where l.flight.id = :id")
	Collection<Leg> findLegsByFlightId(int id);

	@Query("select a from Airport a")
	Collection<Airport> findAirports();

	@Query("select a from Aircraft a where a.status = 'ACTIVE'")
	Collection<Aircraft> findActiveAircrafts();

	@Query("select l from Leg l where l.aircraft.airline.id = :id")
	Collection<Leg> findLegsByAirlineId(int id);

}
