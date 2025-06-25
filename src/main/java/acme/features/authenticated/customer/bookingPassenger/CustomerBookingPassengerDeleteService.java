
package acme.features.authenticated.customer.bookingPassenger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerDeleteService extends AbstractGuiService<Customer, BookingPassenger> {
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
		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);

		if (bookingPassenger != null && passenger != null) {
			boolean bookingIsDraft = bookingPassenger.getBooking().getIsDraft() == true;
			boolean passengerNotChanged = true;
			if (super.getRequest().hasData("passenger")) {
				int actualPassengerId = super.getRequest().getData("passenger", int.class);
				passengerNotChanged = actualPassengerId == passenger.getId();
			}
			super.getResponse().setAuthorised(passenger.getCustomer().getUserAccount().getId() == userAccountId && bookingIsDraft && passengerNotChanged);
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);
		super.getBuffer().addData(bookingPassenger);

	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		super.bindObject(bookingPassenger, "passenger");
	}

	@Override
	public void validate(final BookingPassenger bookingPassenger) {
	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		BookingPassenger bookingPassengerToDelete = this.repository.findBookingPassengerByBookingIdAndPassengerId(bookingPassenger.getBooking().getId(), bookingPassenger.getPassenger().getId());
		this.repository.delete(bookingPassengerToDelete);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		Dataset dataset;
		SelectChoices passengers = null;
		int customerId;
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<Passenger> publishedPassengers = this.repository.findAllPublishedPassengersByCustomerId(customerId);
		Collection<Passenger> passengerAlreadyInBooking = this.repository.findPassengersByBookingId(bookingPassenger.getBooking().getId());

		List<Passenger> passengersNotInBooking = publishedPassengers.stream().filter(p -> !passengerAlreadyInBooking.contains(p)).toList();

		dataset = super.unbindObject(bookingPassenger, "booking", "passenger", "id");
		dataset.put("bookingLocatorCode", bookingPassenger.getBooking().getLocatorCode());
		dataset.put("bookingId", bookingPassenger.getBooking().getId());

		List<Passenger> listPassengersNotInBooking = new ArrayList<>(passengersNotInBooking);
		//if (!passengersNotInBooking.isEmpty()) {
		if (!passengersNotInBooking.contains(bookingPassenger.getPassenger()))
			listPassengersNotInBooking.add(bookingPassenger.getPassenger());
		passengers = SelectChoices.from(listPassengersNotInBooking, "fullName", bookingPassenger.getPassenger());
		dataset.put("passenger", passengers);

		super.getResponse().addData(dataset);
	}

}
