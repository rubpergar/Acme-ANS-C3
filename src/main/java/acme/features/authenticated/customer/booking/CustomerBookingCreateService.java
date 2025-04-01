
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
		//		Booking booking;
		//		int bookingId;
		//		int userAccountId;
		//
		//		bookingId = super.getRequest().getData("id", int.class);
		//		booking = this.repository.findBookingById(bookingId);
		//
		//		userAccountId = super.getRequest().getPrincipal().getAccountId();
		//		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId);
		//
		//		if (booking.getFlight().getIsDraft() && booking.getFlight() != null)
		//			super.state(booking.getIsDraft(), "*", "customer.booking.form.error.notDraftFlight", "flight");

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
		booking.getPrice();
		booking.setLastNibble(null);
		booking.setIsDraft(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "flight", "travelClass", "lastNibble");
	}

	// ??????
	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		// Verificar que el locatorCode es único
		boolean locatorCodeStatus = this.repository.findBookingByLocatorCode(booking.getLocatorCode()) == null;
		super.state(locatorCodeStatus, "locatorCode", "acme.validation.booking.repeated-locatorCode.message");

		// Verificar que PurchaseMoment no cambia
		boolean purchaseMomentStatus = booking.getPurchaseMoment().equals(MomentHelper.getCurrentMoment());
		super.state(purchaseMomentStatus, "purchaseMoment", "acme.validation.booking.incorrect-purchaseMoment.message");

		// Verificar que el price no cambia
		boolean priceStatus = booking.getPrice().equals(booking.getPrice());
		super.state(priceStatus, "price", "acme.validation.booking.incorrect-price.message");

		// Verificar que el flight está publicado
		boolean flightStatus = booking.getFlight().getIsDraft() == false;
		super.state(flightStatus, "flight", "acme.validation.booking.flight-draft.message");

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
		SelectChoices flights = SelectChoices.from(nonDraftFlights, "tag", booking.getFlight());
		Dataset dataset;
		dataset = super.unbindObject(booking, "locatorCode", "flight", "purchaseMoment", "travelClass", "lastNibble", "isDraft");
		dataset.put("travelClass", travelClasses);
		dataset.put("flight", flights);
		dataset.put("price", booking.getPrice());
		super.getResponse().addData(dataset);
	}

}
