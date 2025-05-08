
package acme.features.authenticated.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerShowService extends AbstractGuiService<Customer, BookingPassenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Passenger passenger;
		int id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerByBookingPassengerId(id);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(passenger.getCustomer().getUserAccount().getId() == userAccountId);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);
		super.getBuffer().addData(bookingPassenger);

	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {

		assert bookingPassenger != null;
		Dataset dataset;
		SelectChoices passengers;
		int customerId;
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		passengers = SelectChoices.from(this.repository.findAllPublishedPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		dataset.put("passenger", passengers);
		dataset.put("bookingLocatorCode", bookingPassenger.getBooking().getLocatorCode());
		dataset.put("bookingIsDraft", bookingPassenger.getBooking().getIsDraft());
		dataset.put("passengerId", bookingPassenger.getPassenger().getId());

		super.getResponse().addData(dataset);
	}
}
