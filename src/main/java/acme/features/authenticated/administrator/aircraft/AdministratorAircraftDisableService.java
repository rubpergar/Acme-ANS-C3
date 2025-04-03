
package acme.features.authenticated.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAircraftDisableService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAircraftRepository repository;

	// AbstractUpdateService<Administrator, Aircraft> interface --------------


	@Override
	public void authorise() {
		int aircraftId = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.getAircraftById(aircraftId);
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && aircraft != null;
		super.getResponse().setAuthorised(hasAuthority);
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
		aircraft.setAirline(aircraft.getAirline());
		aircraft.setStatus(AircraftStatus.MAINTENANCE);
		aircraft.setDetails(aircraft.getDetails());
		aircraft.setCargoWeight(aircraft.getCargoWeight());
		aircraft.setCapacity(aircraft.getCapacity());
		aircraft.setRegistrationNumber(aircraft.getRegistrationNumber());
		aircraft.setModel(aircraft.getModel());
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		SelectChoices selectedAirlines;
		Collection<Airline> airlines;
		airlines = this.repository.findAllAirlines();
		selectedAirlines = SelectChoices.from(airlines, "name", aircraft.getAirline());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("status", choices);
		dataset.put("airlines", selectedAirlines);
		dataset.put("airline", selectedAirlines.getSelected().getKey());

		super.getResponse().addData(dataset);
	}

}
