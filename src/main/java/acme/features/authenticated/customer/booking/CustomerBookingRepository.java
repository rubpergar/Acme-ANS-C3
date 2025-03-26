
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findAllBookingsByCustomer(int customerId);

	Booking findBookingById(int id);

	@Query("SELECT bp.passenger FROM BookingPassenger bp WHERE bp.booking.id = :bookingId")
	Collection<Passenger> findAllPassengersByBookingId(int bookingId);

	@Query("select c from Customer c where c.id = :id")
	Customer findCustomerById(int id);

	@Query("select b.lastNibble from Booking b where b.id = :id")
	String findLastNibbleById(int id);
}
