
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
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);

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
