
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
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		Integer bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		Integer customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = status && booking.getCustomer().getId() == customerId && booking.getIsDraft();

		// FLIGHT
		if (super.getRequest().getMethod().equals("POST")) {
			Integer flightId = super.getRequest().getData("flight", int.class);
			if (flightId != 0 && flightId != null && booking.getFlight() != null) {
				Flight flight = this.repository.findFlightById(flightId);
				status = status && flight != null && !flight.getIsDraft();
			}
		}

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
		// Verificar que el locatorCode es único
		Booking bookingExistingLocatorCode = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		boolean locatorCodeStatus = bookingExistingLocatorCode == null || bookingExistingLocatorCode.getId() == booking.getId();
		super.state(locatorCodeStatus, "locatorCode", "acme.validation.booking.repeated-locatorCode.message");

	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		List<Flight> validFlights = nonDraftFlights.stream().filter(f -> f.getScheduledDeparture().after(booking.getPurchaseMoment())).toList();

		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		SelectChoices flights = SelectChoices.from(validFlights, "flightDistinction", booking.getFlight());
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "lastNibble", "isDraft");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("price", booking.getPrice());
		super.getResponse().addData(dataset);
	}

}
