
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
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@GuiService
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAircraftRepository	repository;

	@Autowired
	protected LegRepository						legRepository;

	// AbstractUpdateService<Administrator, Aircraft> interface --------------


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
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
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

		String airlineCode = aircraft.getAirline().getCodeIATA();

		Collection<Leg> legs = this.repository.getLegsByAircraft(aircraft);
		for (Leg leg : legs) {
			String flightNumberSuffix = leg.getFlightNumber().substring(3);
			String newFlightNumber = airlineCode + flightNumberSuffix;
			leg.setFlightNumber(newFlightNumber);
			this.legRepository.save(leg);
		}
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("status", choices);

		super.getResponse().addData(dataset);
	}

}
