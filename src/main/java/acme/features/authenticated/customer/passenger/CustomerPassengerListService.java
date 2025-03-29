
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------

	//	@Override
	//	public void authorise() {
	//		boolean status;
	//		int masterId;
	//		Booking booking;
	//
	//		masterId = super.getRequest().getData("masterId", int.class);
	//		booking = this.repository.findBookingById(masterId);
	//		status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
	//
	//		super.getResponse().setAuthorised(status);
	//	}
	//
	//	@Override
	//	public void load() {
	//		Collection<Passenger> passengers;
	//		int masterId;
	//
	//		masterId = super.getRequest().getData("masterId", int.class);
	//		passengers = this.repository.findPassengersByBookingId(masterId);
	//
	//		super.getBuffer().addData(passengers);
	//	}
	//
	//	//	@Override
	//	//	public void unbind(final Passenger passenger) {
	//	//		//assert passenger != null;
	//	//
	//	//		Dataset dataset;
	//	//
	//	//		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "isDraft");
	//	//		super.getResponse().addData(dataset);
	//	//	}
	//
	//	@Override
	//	public void unbind(final Collection<Passenger> passengers) {
	//		int masterId;
	//		Booking booking;
	//		final boolean showCreate;
	//
	//		masterId = super.getRequest().getData("masterId", int.class);
	//		booking = this.repository.findBookingById(masterId);
	//		showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
	//
	//		super.getResponse().addGlobal("masterId", masterId);
	//		super.getResponse().addGlobal("showCreate", showCreate);
	//	}

	//	@Override
	//	public void authorise() {
	//		boolean status;
	//		int masterId;
	//		Booking booking;
	//
	//		masterId = super.getRequest().getData("masterId", int.class);
	//		booking = this.repository.findBookingById(masterId);
	//		status = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());
	//
	//		super.getResponse().setAuthorised(status);
	//	}


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		passengers = this.repository.findPassengersByBookingId(masterId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "isDraft");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int masterId;
		Booking booking;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(masterId);
		showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
