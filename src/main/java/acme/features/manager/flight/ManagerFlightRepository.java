
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f where f.id = :flightId")
	Flight getFlightById(int flightId);

	@Query("select f from Flight f where f.airlineManager.id = :managerId")
	Collection<Flight> getFlightsByManagerId(int managerId);

	@Query("select m from Manager m where m.id = :managerId")
	Manager getManagerById(int managerId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	Collection<Leg> getLegsByFlight(int flightId);

}
