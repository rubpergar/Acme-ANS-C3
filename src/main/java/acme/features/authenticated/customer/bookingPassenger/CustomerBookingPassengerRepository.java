
package acme.features.authenticated.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingPassengerRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findAllBookingsByCustomer(int customerId);

	@Query("SELECT bp.passenger FROM BookingPassenger bp WHERE bp.booking.customer.id = :customerId")
	Collection<Passenger> findAllPassengersByCustomerId(int customerId);

}
