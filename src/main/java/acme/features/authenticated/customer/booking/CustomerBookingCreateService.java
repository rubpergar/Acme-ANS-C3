
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
		super.getResponse().setAuthorised(true);
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
		booking.setFlight(null);
		booking.setPurchaseMoment(currentMoment);
		booking.setTravelClass(null);
		booking.setPrice(null);
		booking.setLastNibble(null);
		booking.setIsDraft(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "price", "lastNibble");
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

		List<Flight> nonDraftFlights = this.repository.findNotDraftFlights().stream().toList();
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "id", booking.getFlight());
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "price", "lastNibble");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("isDraft", true);
		super.getResponse().addData(dataset);
	}

}
