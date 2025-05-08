
package acme.features.authenticated.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.passenger.Passenger;

@Repository
public interface CustomerBookingPassengerRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("select p from Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("select b from Booking b where b.customer.id = :customerId")
	Collection<Booking> findAllBookingsByCustomer(int customerId);

	@Query("select p from Passenger p where p.customer.id = :customerId and p.isDraft = false")
	Collection<Passenger> findAllPublishedPassengersByCustomerId(int customerId);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<BookingPassenger> findBookingPassengersByBookingId(int bookingId);

	@Query("select bp.passenger from BookingPassenger bp where bp.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select p from Passenger p where p.id = :id and p.customer.id = :customerId")
	Passenger findPassengerByIdAndCustomerId(int id, int customerId);

	@Query("select bp from BookingPassenger bp where bp.id = :id")
	BookingPassenger findBookingPassengerById(int id);

	@Query("select bp from BookingPassenger bp where bp.booking.id = :bookingId and bp.passenger.id = :passengerId")
	BookingPassenger findBookingPassengerByBookingIdAndPassengerId(int bookingId, int passengerId);

	@Query("select bp.passenger from BookingPassenger bp where bp.id = :bookingPassengerId")
	Passenger findPassengerByBookingPassengerId(int bookingPassengerId);

}
