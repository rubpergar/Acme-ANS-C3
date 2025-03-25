
package acme.features.authenticated.manager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	Collection<Flight> getAllFlights();

	@Query("select f from Flight f where f.id = :flightId")
	Flight getFlightById(int flightId);

	@Query("select f from Flight f where f.airlineManager.id = :managerId")
	Collection<Flight> getFlightsByManagerId(int managerId);

	@Query("select m from Manager m where m.id = :managerId")
	Manager getManagerById(int managerId);

	@Query("select m from Manager m where m.userAccount.id = :userAccountId")
	Manager getManagerByUserAccountId(int userAccountId);

	@Query("select m from Manager m where m.userAccount.username = :username")
	Manager getManagerByUsername(String username);

	@Query("select l from Booking l where l.flight.id = :flightId")
	Collection<Booking> getBookingsByFlight(int flightId);

	@Query("select l from BookingPassenger l where l.booking.id = :bookingId")
	Collection<BookingPassenger> getBookingPassengerByBooking(int bookingId);

	@Query("select l from Leg l where l.flight.id = :flightId")
	Collection<Leg> getLegsByFlight(int flightId);

	@Query("select a from FlightAssignment a where a.leg.id = :legId")
	Collection<FlightAssignment> getAssignmentsByLeg(int legId);

	@Query("select a from ActivityLog a where a.flightAssignment.id = :assignmentId")
	Collection<ActivityLog> getActivityLogsByFlightAssignment(int assignmentId);

	/*
	 * @Query("select c from Claim c where c.leg.id = :legId")
	 * Collection<Claim> getClaimsByLeg(int legId);
	 * 
	 * @Query("select t from TrackingLog t where t.claim.id = :claimId")
	 * Collection<TrackingLog> getTrackingLogsByClaim(int claimId);
	 */

}
