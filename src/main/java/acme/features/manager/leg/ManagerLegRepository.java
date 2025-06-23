
package acme.features.manager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airports.Airport;
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

	@Query("select a from Airport a")
	Collection<Airport> findAllAirports();

	@Query("select a from Aircraft a where a.status = :status")
	Collection<Aircraft> findAllAircraftsByStatus(@Param("status") AircraftStatus status);

	@Query("select a from Aircraft a where a.id = :aircraftId")
	Aircraft findAircraftById(int aircraftId);

	@Query("select a from Airport a where a.id = :departureAirportId")
	Airport findAirportById(int departureAirportId);

	@Query("select f from Flight f where f.id = :flightId")
	Flight getFlightById(int flightId);

	@Query("select a from Aircraft a where a.registrationNumber = :registrationNumber")
	Aircraft getAircraftByRegistrationNumber(String registrationNumber);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLegs();

}
