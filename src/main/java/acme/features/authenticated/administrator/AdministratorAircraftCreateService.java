
package acme.features.authenticated.administrator;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Aircraft aircraft;

		aircraft = new Aircraft();
		aircraft.setModel("");
		aircraft.setRegistrationNumber("");
		aircraft.setCapacity(0);
		aircraft.setCargoWeight(0);
		aircraft.setStatus(AircraftStatus.ACTIVE);
		aircraft.setDetails("");

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		assert aircraft != null;
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "details");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		assert aircraft != null;
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		assert aircraft != null;
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;

		SelectChoices choices;
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		Dataset dataset;
		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "details");
		dataset.put("status", choices);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		super.getResponse().addData(dataset);
	}

}
