
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

		boolean status = booking.getCustomer().getUserAccount().getId() == userAccountId && booking.getIsDraft();

		//if (super.getRequest().getMethod().equals("POST")) {
		Integer flightId = super.getRequest().getData("flight", Integer.class);
		Flight flight = flightId != null ? this.repository.findFlightById(flightId) : null;

		boolean invalidFlight = flight == null || flight.getIsDraft();

		if (invalidFlight)
			status = false;
		//}

		//super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId && validDraft && validFlight);
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
		assert booking != null;
		super.bindObject(booking, "locatorCode", "flight", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		// Verificar que el locatorCode es único
		boolean locatorCodeStatus = this.repository.findBookingsByLocatorCode(booking.getLocatorCode()).size() == 1;
		super.state(locatorCodeStatus, "locatorCode", "acme.validation.booking.repeated-locatorCode.message");

		// Verificar que el flight está publicado
		boolean flightDraftStatus = booking.getFlight().getIsDraft() == false;
		super.state(flightDraftStatus, "flight", "acme.validation.booking.flight-draft.message");

		// Verificar que el flight no es null
		boolean flightNullStatus = this.repository.findNotDraftFlights().contains(booking.getFlight());
		super.state(flightNullStatus, "flight", "acme.validation.booking.notExisting-flight.message");

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
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "flightDistinction", booking.getFlight());
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
