
package acme.features.authenticated.customer.bookingPassenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerListService extends AbstractGuiService<Customer, BookingPassenger> {

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Booking booking;
		int id;
		id = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(id);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();

		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId);
	}

	@Override
	public void load() {
		Collection<BookingPassenger> bookingPassengers;

		Integer masterId = super.getRequest().getData("masterId", int.class);
		bookingPassengers = this.repository.findBookingPassengersByBookingId(masterId);

		super.getResponse().addGlobal("masterId", masterId);

		super.getBuffer().addData(bookingPassengers);
	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {
		Dataset dataset;

		dataset = super.unbindObject(bookingPassenger, "passenger", "booking");
		dataset.put("fullName", bookingPassenger.getPassenger().getFullName());
		dataset.put("email", bookingPassenger.getPassenger().getEmail());
		dataset.put("passportNumber", bookingPassenger.getPassenger().getPassportNumber());
		dataset.put("dateOfBirth", bookingPassenger.getPassenger().getDateOfBirth());
		dataset.put("bookingIsDraft", bookingPassenger.getBooking().getIsDraft());

		super.getResponse().addGlobal("bookingIsDraft", bookingPassenger.getBooking().getIsDraft());
		dataset.put("bookingLocatorCode", bookingPassenger.getBooking().getLocatorCode());
		dataset.put("bookingId", bookingPassenger.getBooking().getId());

		super.getResponse().addData(dataset);
	}

}
