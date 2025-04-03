
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
import acme.entities.passenger.Passenger;
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
		boolean validFlight = this.repository.findNotDraftFlights().contains(booking.getFlight());
		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId && validFlight);

		if (booking.getIsDraft())
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
		super.bindObject(booking, "locatorCode", "flight", "travelClass", "lastNibble");
	}

	// ????
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
		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "tag", booking.getFlight());
		List<Passenger> passengers = this.repository.findAllPassengersByBookingId(booking.getId()).stream().toList();
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "lastNibble", "isDraft");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("passenger", !passengers.isEmpty());
		dataset.put("price", booking.getPrice());
		super.getResponse().addData(dataset);
	}

}
