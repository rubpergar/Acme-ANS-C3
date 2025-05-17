
package acme.features.authenticated.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerDeleteService extends AbstractGuiService<Customer, BookingPassenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		BookingPassenger bookingPassenger;
		int bookingId;
		int userAccountId;
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		bookingId = super.getRequest().getData("id", int.class);
		bookingPassenger = this.repository.findBookingPassengerById(bookingId);

		boolean status = bookingPassenger.getBooking().getCustomer().getUserAccount().getId() == userAccountId;

		if (!bookingPassenger.getBooking().getIsDraft())
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);
		super.getBuffer().addData(bookingPassenger);

	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		super.bindObject(bookingPassenger, "passenger");
	}

	@Override
	public void validate(final BookingPassenger bookingPassenger) {
	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		this.repository.delete(bookingPassenger);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {

		assert bookingPassenger != null;
		Dataset dataset;
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		super.getResponse().addData(dataset);
	}

}
