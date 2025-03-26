
package acme.features.authenticated.administrator;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAircraftRepository repository;

	// AbstractListService<Administrator, Aircraft> interface -----------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int aircraftId;

		aircraftId = this.getRequest().getData("id", int.class);
		aircraft = this.repository.getAircraftById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		assert aircraft != null;
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
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
		aircraft.setStatus(AircraftStatus.MAINTENANCE);
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		Dataset dataset;

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");

		super.getResponse().addData(dataset);
	}

}
