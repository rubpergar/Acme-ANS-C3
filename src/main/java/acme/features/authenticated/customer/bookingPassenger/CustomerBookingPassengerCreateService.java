
package acme.features.authenticated.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerCreateService extends AbstractGuiService<Customer, BookingPassenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}
	//NO PUEDEN SER DRAFT NI BOOKING NI PASSENGER

	@Override
	public void load() {
		BookingPassenger bookingPassenger;
		Booking booking;

		booking = this.repository.findBookingById(super.getRequest().getData("masterId", int.class));

		bookingPassenger = new BookingPassenger();
		bookingPassenger.setBooking(booking);
		bookingPassenger.setPassenger(null);

		super.getBuffer().addData(bookingPassenger);
	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		super.bindObject(bookingPassenger, "booking", "passenger");
	}

	// ??????
	@Override
	public void validate(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		this.repository.save(bookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		Dataset dataset;
		SelectChoices passengers;
		int customerId;
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = SelectChoices.from(this.repository.findAllPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengers);
		super.getResponse().addData(dataset);
	}

}
