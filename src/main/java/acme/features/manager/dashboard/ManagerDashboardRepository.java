
package acme.features.manager.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@Repository
public interface ManagerDashboardRepository extends AbstractRepository {

	@Query("select m from Manager m")
	Collection<Manager> findAirlineManagers();

	@Query("select m from Manager m where m.id = :id")
	Manager findAirlineManagerById(int id);

	@Query("select f from Flight f where f.airlineManager.id = :id")
	Collection<Flight> findFlightsByAirlineManagerId(int id);

	@Query("select l from Leg l where l.flight.id = :id")
	Collection<Leg> findLegsByFlightId(int id);

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

}
