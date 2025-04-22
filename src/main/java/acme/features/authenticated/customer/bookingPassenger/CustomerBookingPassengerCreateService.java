
package acme.features.authenticated.customer.bookingPassenger;

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

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		boolean isCustomer = customerId == booking.getCustomer().getId();
		boolean isDraft = booking.getIsDraft();
		boolean validPassenger = true;

		if (super.getRequest().getMethod().equals("POST")) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			Passenger passenger = passengerId != null ? this.repository.findPassengerByIdAndCustomerId(passengerId, customerId) : null;

			boolean invalidPassenger = (passenger == null || passenger.getIsDraft()) && passengerId != null;

			if (invalidPassenger)
				validPassenger = false;
		}

		super.getResponse().setAuthorised(isCustomer && isDraft && validPassenger);

		if (!isDraft)
			super.state(false, "*", "customer.booking.form.error.publishedBooking", "booking");
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

		super.bindObject(bookingPassenger, "booking");
	}

	@Override
	public void validate(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;

		boolean passengerStatus = true;
		boolean bookingPassengerStatus = true;

		if (bookingPassenger.getPassenger() != null) {
			// Verificar que el pasagero está publicadods
			passengerStatus = this.repository.findPassengerById(bookingPassenger.getPassenger().getId()).getIsDraft() == false;
			// Verificar que el bookingPassenger no existe
			bookingPassengerStatus = this.repository.findBookingPassengerByBookingIdAndPassengerId(bookingPassenger.getBooking().getId(), bookingPassenger.getPassenger().getId()) == null;
		}
		super.state(passengerStatus, "passenger", "acme.validation.booking-passenger.notPublishedPassenger.message");
		super.state(bookingPassengerStatus, "bookingPassenger", "acme.validation.booking-passenger.notExistingBookingPassenger.message");
	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		bookingPassenger.setPassenger(bookingPassenger.getPassenger());
		this.repository.save(bookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		Dataset dataset;
		SelectChoices passengers;
		int customerId;
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = SelectChoices.from(this.repository.findAllPublishedPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengers);
		dataset.put("bookingLocatorCode", bookingPassenger.getBooking().getLocatorCode());

		int masterId = super.getRequest().getData("masterId", int.class);
		super.getResponse().addGlobal("masterId", masterId);

		super.getResponse().addData(dataset);
	}

}
