
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Passenger passenger;
		int id;
		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(passenger.getCustomer().getUserAccount().getId() == userAccountId && passenger.getIsDraft());
	}

	@Override
	public void load() {
		Passenger passenger;
		int passengerId;

		passengerId = this.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	// ???
	@Override
	public void validate(final Passenger passenger) {
		assert passenger != null;
	}

	@Override
	public void perform(final Passenger passenger) {
		assert passenger != null;
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		assert passenger != null;
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
		super.getResponse().addData(dataset);
	}

}
