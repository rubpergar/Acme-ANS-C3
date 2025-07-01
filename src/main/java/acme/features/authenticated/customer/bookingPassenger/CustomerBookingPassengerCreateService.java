
package acme.features.authenticated.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerCreateService extends AbstractGuiService<Customer, BookingPassenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Booking booking;
		int bookingId;
		int customerId;

		bookingId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(bookingId);

		if (booking != null) {
			customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			boolean isCustomer = customerId == booking.getCustomer().getId();
			boolean isDraft = booking.getIsDraft();
			boolean validPassenger = true;

			if (super.getRequest().getMethod().equals("POST")) {
				Integer passengerId = super.getRequest().getData("passenger", Integer.class);
				if (passengerId != 0) {
					// Pasajero no existente o es borrador
					Passenger passenger = this.repository.findPassengerByIdAndCustomerId(passengerId, customerId);

					boolean invalidPassenger = (passenger == null || passenger.getIsDraft()) && passengerId != null;

					if (invalidPassenger)
						validPassenger = false;
					// Pasajero ya existe en el booking
					boolean existingBookingPassenger = this.repository.findBookingPassengerByBookingIdAndPassengerId(bookingId, passengerId) != null;
					if (existingBookingPassenger)
						validPassenger = false;

				}
			}

			super.getResponse().setAuthorised(isCustomer && isDraft && validPassenger);

			if (!isDraft)
				super.state(false, "*", "customer.booking.form.error.publishedBooking", "booking");
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		BookingPassenger bookingPassenger;
		Booking booking;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);

		bookingPassenger = new BookingPassenger();
		bookingPassenger.setBooking(booking);

		super.getBuffer().addData(bookingPassenger);
	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("passenger", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		bookingPassenger.setPassenger(passenger);

		super.bindObject(bookingPassenger);
	}

	@Override
	public void validate(final BookingPassenger bookingPassenger) {

	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		bookingPassenger.setPassenger(bookingPassenger.getPassenger());
		this.repository.save(bookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		Dataset dataset;
		SelectChoices passengers;
		int customerId;
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<Passenger> publishedPassengers = this.repository.findAllPublishedPassengersByCustomerId(customerId);
		Collection<Passenger> passengerAlreadyInBooking = this.repository.findPassengersByBookingId(bookingPassenger.getBooking().getId());

		Collection<Passenger> passengersNotInBooking = publishedPassengers.stream().filter(p -> !passengerAlreadyInBooking.contains(p)).toList();

		passengers = SelectChoices.from(passengersNotInBooking, "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengers);
		dataset.put("bookingLocatorCode", bookingPassenger.getBooking().getLocatorCode());
		dataset.put("bookingId", bookingPassenger.getBooking().getId());

		int masterId = super.getRequest().getData("masterId", int.class);
		super.getResponse().addGlobal("masterId", masterId);

		super.getResponse().addData(dataset);
	}

}
