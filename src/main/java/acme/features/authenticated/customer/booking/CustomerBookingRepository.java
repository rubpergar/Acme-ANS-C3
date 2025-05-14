
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.flights.Flight;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id =:bookingId")
	Booking findBookingById(int bookingId);

	@Query("SELECT bp.passenger FROM BookingPassenger bp WHERE bp.booking.id = :bookingId")
	Collection<Passenger> findAllPassengersByBookingId(int bookingId);

	@Query("select c from Customer c where c.userAccount.id = :id")
	Customer findCustomerByuserAccountId(int id);

	@Query("select b.lastNibble from Booking b where b.id = :id")
	String findLastNibbleById(int id);

	@Query("select f from Flight f where f.isDraft = false")
	Collection<Flight> findNotDraftFlights();

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findAllBookingsByCustomer(int customerId);

	@Query("select br.passenger from BookingPassenger br where br.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(int bookingId);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Collection<Booking> findBookingsByLocatorCode(String locatorCode);

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select f from Flight f where f.id = :id and f.isDraft = false")
	Flight findPublishedFlightById(int id);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findAllBookingPassengersByBookingId(int bookingId);

}

