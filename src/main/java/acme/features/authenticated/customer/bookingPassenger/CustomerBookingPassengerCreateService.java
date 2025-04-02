
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

	//	@Override
	//	public void authorise() {
	//		//		super.getResponse().setAuthorised(true);
	//
	//		Booking booking;
	//		int bookingId;
	//		int customerId;
	//
	//		bookingId = super.getRequest().getData("id", int.class);
	//		booking = this.repository.findBookingById(bookingId);
	//		boolean isDraft = booking.getIsDraft();
	//
	//		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
	//		super.getResponse().setAuthorised(customerId == booking.getCustomer().getId() && isDraft);
	//
	//		if (!isDraft)
	//			super.state(false, "*", "customer.booking.form.error.draftBooking", "booking");
	//	}


	@Override
	public void authorise() {
		Booking booking;
		int bookingId;
		int customerId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		// Solo el cliente dueño puede acceder y solo si el booking está en draft
		boolean isOwner = customerId == booking.getCustomer().getId();
		boolean isDraft = booking.getIsDraft();

		super.getResponse().setAuthorised(isOwner && isDraft);

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
		bookingPassenger.setPassenger(null);

		super.getBuffer().addData(bookingPassenger);
	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		super.bindObject(bookingPassenger, "booking", "passenger");
	}

	@Override
	public void validate(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;

		// Verificar que el pasagero está publicado
		boolean passengerStatus = this.repository.findPassengerById(bookingPassenger.getPassenger().getId()).getIsDraft() == false;
		super.state(passengerStatus, "passenger", "acme.validation.booking-passenger.notPublishedPassenger.message");
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
		passengers = SelectChoices.from(this.repository.findAllPublishedPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengers);
		super.getResponse().addData(dataset);
	}

}
