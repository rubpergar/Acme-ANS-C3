
package acme.features.authenticated.customer.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		Integer bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		if (booking != null)
			status = status && booking.getCustomer().getId() == customerId && booking.getIsDraft();
		else
			status = false;

		super.getResponse().setAuthorised(status);
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
		super.bindObject(booking, "locatorCode", "flight", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {

	}

	@Override
	public void perform(final Booking booking) {
		this.repository.findAllBookingPassengersByBookingId(booking.getId()).forEach(bp -> this.repository.delete(bp));

		this.repository.delete(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		List<Flight> validFlights = nonDraftFlights.stream().filter(f -> f.getScheduledDeparture().after(booking.getPurchaseMoment())).toList();
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		dataset = super.unbindObject(booking, "locatorCode", "customer", "flight", "purchaseMoment", "travelClass", "lastNibble", "price", "isDraft");

		dataset.put("travelClass", travelClasses);

		if (!validFlights.isEmpty()) {
			SelectChoices flights = SelectChoices.from(validFlights, "flightDistinction", booking.getFlight());
			dataset.put("flight", flights);
		}
		super.getResponse().addData(dataset);
	}

}
