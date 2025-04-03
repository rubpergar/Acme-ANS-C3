
package acme.entities.legs;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l.scheduledDeparture from Leg l where l.flight.id = :flightId AND l.scheduledDeparture IS NOT NULL order by l.scheduledDeparture")
	Collection<Date> getScheduledDeparture(int flightId);

	@Query("select l.scheduledArrival from Leg l where l.flight.id = :flightId AND l.scheduledArrival IS NOT NULL order by l.scheduledDeparture desc")
	Collection<Date> getScheduledArrival(int flightId);

	@Query("select l.departureAirport.city from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	Collection<String> getOriginCity(Integer flightId);

	@Query("select l.arrivalAirport.city from Leg l where l.flight.id = :flightId order by l.scheduledDeparture desc")
	Collection<String> getDestinationCity(Integer flightId);

	@Query("select count(l) from Leg l where l.flight.id = :flightId")
	Integer getNumberLegsByFlight(Integer flightId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture")
	Collection<Leg> getLegsByFlight(Integer flightId);

	@Query("select l from Leg l where l.flightNumber = :flightNumber")
	Optional<Leg> findLegByFlightNumber(String flightNumber);

}
