
package acme.features.authenticated.manager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :legId")
	Leg getLegById(int legId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> getLegByFlightId(int flightId);

	@Query("select l.flight from Leg l where l.id = :legId")
	Flight getFlightByLegId(int legId);

}
