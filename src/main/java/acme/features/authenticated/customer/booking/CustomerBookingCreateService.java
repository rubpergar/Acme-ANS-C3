
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		//		boolean status;
		//
		//		status = !super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Booking booking;
		int customerId;
		Customer customer;

		customerId = super.getRequest().getPrincipal().getAccountId();
		customer = this.repository.findCustomerById(customerId);

		booking = new Booking();
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "locatorCode", "purchaseMoment", "travelClass", "price", "isDraft");
	}

	// ??????
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
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "price", "isDraft");
		super.getResponse().addData(dataset);
	}

}
