
package acme.features.authenticated.customer.booking;

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
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {
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

		boolean status = booking.getCustomer().getUserAccount().getId() == userAccountId;

		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId);

		if (!booking.getIsDraft())
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
		assert booking != null;
		super.bindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		//Tengo que comprobar the last credit card nibble has been stored. 
		String lastNibble = this.repository.findLastNibbleById(booking.getId());
		super.state(!lastNibble.isEmpty(), "*", "customer.project.publish.error.lastNibbleNotPublished");

		//Comprobar que el booking no tiene pasajeros
		List<Passenger> passengers = this.repository.findAllPassengersByBookingId(booking.getId()).stream().toList();
		super.state(passengers.size() > 0, "*", "customer.project.publish.error.passengerNotEmpty");

		//Comprobar que el precio no es 0
		super.state(booking.getPrice().getAmount() != 0.0, "*", "customer.project.publish.error.priceNotNull");

		//Comprobar que la fecha de compra es anterior a la fecha de vuelo
		boolean flightIsAfterStatus = booking.getFlight().getScheduledArrival().after(MomentHelper.getCurrentMoment());
		super.state(flightIsAfterStatus, "flight", "acme.validation.booking.after-flight.message");

	}

	@Override
	public void perform(final Booking booking) {
		assert booking != null;
		booking.setIsDraft(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "flightDistinction", booking.getFlight());
		List<Passenger> passengers = this.repository.findAllPassengersByBookingId(booking.getId()).stream().toList();
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "price", "lastNibble");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("passenger", !passengers.isEmpty());
		dataset.put("isDraft", booking.getIsDraft());

		super.getResponse().addData(dataset);
	}

}
