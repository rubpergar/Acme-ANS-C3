
package acme.features.authenticated.customer.passenger;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		Date currentMoment;
		int customerId;
		Customer customer;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		currentMoment = MomentHelper.getCurrentMoment();

		passenger = new Passenger();
		passenger.setFullName("");
		passenger.setEmail("");
		passenger.setPassportNumber("");
		passenger.setDateOfBirth(currentMoment);
		passenger.setSpecialNeeds("");
		passenger.setIsDraft(true);
		passenger.setCustomer(customer);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraft");
		super.getResponse().addData(dataset);
	}
}
