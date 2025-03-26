
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
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

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

		aircraftId = super.getRequest().getData("id", int.class);
		aircraft = this.repository.getAircraftById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;

		SelectChoices choices;
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		Dataset dataset;
		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "details");
		dataset.put("status", choices);

		super.getResponse().addData(dataset);
	}

}
