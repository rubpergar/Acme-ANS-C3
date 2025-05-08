
package acme.features.authenticated.customer.booking;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		if (super.getRequest().getMethod().equals("POST")) {
			Integer flightId = super.getRequest().getData("flight", int.class);
			//FLIGHT
			if (flightId != 0) {
				Flight flight = this.repository.findFlightById(flightId);

				if (flight == null || flight.getIsDraft())
					status = false;
			}
			// TRAVEL CLASS
			String travelClass = super.getRequest().getData("travelClass", String.class);

			if (travelClass != null && !travelClass.equals("0"))
				try {
					TravelClass validTravelClass = TravelClass.valueOf(travelClass);
				} catch (IllegalArgumentException ex) {
					status = false;
				}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int userAccountId;
		Customer customer;
		Date currentMoment;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		customer = this.repository.findCustomerByuserAccountId(userAccountId);
		currentMoment = MomentHelper.getCurrentMoment();

		booking = new Booking();
		booking.setLocatorCode("");
		booking.setPurchaseMoment(currentMoment);
		booking.getPrice();
		booking.setLastNibble(null);
		booking.setIsDraft(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);
		booking.setFlight(flight);

		super.bindObject(booking, "locatorCode", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		// Verificar que el locatorCode es único
		boolean locatorCodeStatus = this.repository.findBookingsByLocatorCode(booking.getLocatorCode()).size() == 0;
		super.state(locatorCodeStatus, "locatorCode", "acme.validation.booking.repeated-locatorCode.message");

		// Verificar que PurchaseMoment no cambia
		boolean purchaseMomentStatus = booking.getPurchaseMoment().equals(MomentHelper.getCurrentMoment());
		super.state(purchaseMomentStatus, "purchaseMoment", "acme.validation.booking.incorrect-purchaseMoment.message");

		// Verificar que el flight está publicado
		boolean flightDraftStatus = true;
		if (booking.getFlight() != null)
			flightDraftStatus = booking.getFlight().getIsDraft() == false;
		super.state(flightDraftStatus, "flight", "acme.validation.booking.flight-draft.message");

		// Verificar que el flight no es null
		boolean flightNullStatus = this.repository.findNotDraftFlights().contains(booking.getFlight());
		super.state(flightNullStatus, "flight", "acme.validation.booking.notExisting-flight.message");

	}

	@Override
	public void perform(final Booking booking) {
		assert booking != null;
		booking.setFlight(booking.getFlight());
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;

		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "flightDistinction", booking.getFlight());
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "lastNibble", "isDraft");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("price", booking.getPrice());
		super.getResponse().addData(dataset);
	}

}
