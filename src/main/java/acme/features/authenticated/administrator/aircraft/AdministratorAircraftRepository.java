
package acme.features.authenticated.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airline.Airline;
import acme.entities.legs.Leg;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("select a from Aircraft a where a.id = :aircraftId")
	Aircraft getAircraftById(int aircraftId);

	@Query("select a from Aircraft a")
	Collection<Aircraft> getAircrafts();

	@Query("select a from Airline a where a.id = :airlineId")
	Airline findAirlineById(int airlineId);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

	@Query("select l from Leg l where l.aircraft = :aircraft")
	Collection<Leg> getLegsByAircraft(Aircraft aircraft);

	@Query("select a from Airline a where a.name = :airline")
	Airline getAirlineByName(String airline);

}
