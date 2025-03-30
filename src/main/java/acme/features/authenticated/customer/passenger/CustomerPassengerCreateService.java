
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
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Passenger passenger;
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();

		passenger = new Passenger();
		passenger.setFullName("");
		passenger.setEmail("");
		passenger.setPassportNumber("");
		passenger.setDateOfBirth(currentMoment);
		passenger.setSpecialNeeds("");
		passenger.setIsDraft(true);

		//no se como asociar el passenger con el booking

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraft");
	}

	// ??????
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
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "isDraft");
		super.getResponse().addData(dataset);
	}

}
