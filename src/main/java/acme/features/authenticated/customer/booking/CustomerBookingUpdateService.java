
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Booking booking;
		int bookingId;
		int userAccountId;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId);

		if (!booking.getIsDraft())
			super.state(booking.getIsDraft(), "*", "customer.booking.form.error.notDraft", "isDraft");
	}

	@Override
	public void load() {
		Booking booking;
		int bookingId;

		bookingId = this.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		assert booking != null;
		super.bindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "isDraft");
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;
	}

	@Override
	public void perform(final Booking booking) {
		assert booking != null;
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		Dataset dataset;

		dataset = super.unbindObject(booking, "locatorCode", "customer", "travelClass", "passengers", "isDraft");

		super.getResponse().addData(dataset);
	}

}
