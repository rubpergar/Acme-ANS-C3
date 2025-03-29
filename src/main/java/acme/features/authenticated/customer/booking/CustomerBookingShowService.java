
package acme.features.authenticated.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Booking booking;
		int id;
		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		final int userAccountId = super.getRequest().getPrincipal().getAccountId();
		final int customerId = booking.getCustomer().getUserAccount().getId();
		super.getResponse().setAuthorised(userAccountId == customerId);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", Integer.class);

		if (id == null)
			throw new IllegalArgumentException("El ID del booking es nulo");

		Booking booking = this.repository.findBookingById(id);

		if (booking == null)
			throw new IllegalArgumentException("Booking con ID " + id + " no encontrado.");

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		Dataset dataset;
		//		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		//		Money price = booking.getPrice();
		//		List<Passenger> passengers = this.repository.findAllPassengersByBookingId(booking.getId()).stream().toList();

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "price", "lastNibble", "isDraft");

		//		dataset.put("passenger", !passengers.isEmpty());
		//		dataset.put("passengers", passengers);
		//		dataset.put("travelClass", travelClasses);
		//		dataset.put("price", price);

		super.getResponse().addData(dataset);
	}

}
