
package acme.features.authenticated.customer.bookingPassenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPassengerDeleteService extends AbstractGuiService<Customer, BookingPassenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {

		// CAMBIAR
		BookingPassenger bookingPassenger;
		int bookingId;
		int userAccountId;
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		bookingId = super.getRequest().getData("id", int.class);
		bookingPassenger = this.repository.findBookingPassengerById(bookingId);

		boolean status = bookingPassenger.getBooking().getCustomer().getUserAccount().getId() == userAccountId;

		if (!bookingPassenger.getBooking().getIsDraft())
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Integer id = super.getRequest().getData("id", int.class);
		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);
		super.getBuffer().addData(bookingPassenger);

	}

	@Override
	public void bind(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		super.bindObject(bookingPassenger, "passenger");
	}

	//	@Override
	//	public void bind(final Flight flight) {
	//		assert flight != null;
	//		flight.setAirlineManager(this.repository.getManagerById(super.getRequest().getPrincipal().getActiveRealm().getId()));
	//		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	//	}

	// ????
	@Override
	public void validate(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
	}

	@Override
	public void perform(final BookingPassenger bookingPassenger) {
		assert bookingPassenger != null;
		this.repository.delete(bookingPassenger);
	}

	//	@Override
	//	public void perform(final Flight flight) {   //borra el vuelo y todos los legs asociados
	//		assert flight != null;
	//
	//		//lo de booking no sirve para nada porque un vuelo no tiene booking si no se ha publicado(y no se puede borrar si esta publicado)
	//
	//		this.repository.getLegsByFlight(flight.getId()).forEach(leg -> {
	//			//pasa lo mismo que antes pero con los assigments y los logs, porque un leg no puede tener eso si el vuelo no se ha llevado a cabo
	//			//y no se puede llevar a cabo sin ser publicado
	//			this.repository.delete(leg);
	//		});
	//
	//		this.repository.delete(flight);
	//	}

	@Override
	public void unbind(final BookingPassenger bookingPassenger) {

		assert bookingPassenger != null;
		Dataset dataset;
		//		SelectChoices passengers;
		//		int customerId;
		//		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		//		passengers = SelectChoices.from(this.repository.findAllPublishedPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
		//		dataset.put("passenger", passengers);
		super.getResponse().addData(dataset);
	}

	//	@Override
	//	public void authorise() {
	//
	//		// CAMBIAR
	//		//		Booking booking;
	//		//		int bookingId;
	//		//		int userAccountId;
	//		//
	//		//		bookingId = super.getRequest().getData("id", int.class);
	//		//		booking = this.repository.findBookingById(bookingId);
	//		//
	//		//		userAccountId = super.getRequest().getPrincipal().getAccountId();
	//		//		boolean validFlight = this.repository.findNotDraftFlights().contains(booking.getFlight());
	//		//		super.getResponse().setAuthorised(booking.getCustomer().getUserAccount().getId() == userAccountId && validFlight);
	//		//
	//		//		if (booking.getIsDraft())
	//		//			super.state(booking.getIsDraft(), "*", "customer.booking.form.error.notDraft", "isDraft");
	//		super.getResponse().setAuthorised(true);
	//	}
	//
	//	@Override
	//	public void load() {
	//		Integer id = super.getRequest().getData("id", int.class);
	//		BookingPassenger bookingPassenger = this.repository.findBookingPassengerById(id);
	//		super.getBuffer().addData(bookingPassenger);
	//
	//	}
	//
	//	@Override
	//	public void bind(final BookingPassenger bookingPassenger) {
	//		assert bookingPassenger != null;
	//		super.bindObject(bookingPassenger, "passenger");
	//	}
	//
	//	// ????
	//	@Override
	//	public void validate(final BookingPassenger bookingPassenger) {
	//		assert bookingPassenger != null;
	//	}
	//
	//	@Override
	//	public void perform(final BookingPassenger bookingPassenger) {
	//		assert bookingPassenger != null;
	//		this.repository.save(bookingPassenger);
	//	}
	//
	//	@Override
	//	public void unbind(final BookingPassenger bookingPassenger) {
	//
	//		assert bookingPassenger != null;
	//		Dataset dataset;
	//		SelectChoices passengers;
	//		int customerId;
	//		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
	//		passengers = SelectChoices.from(this.repository.findAllPublishedPassengersByCustomerId(customerId), "fullName", bookingPassenger.getPassenger());
	//		dataset = super.unbindObject(bookingPassenger, "booking", "passenger");
	//		dataset.put("passenger", passengers);
	//		dataset.put("bookingIsDraft", bookingPassenger.getBooking().getIsDraft());
	//		super.getResponse().addData(dataset);
	//	}
}
