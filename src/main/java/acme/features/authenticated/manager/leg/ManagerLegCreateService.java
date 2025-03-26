
package acme.features.authenticated.manager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.authenticated.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	repositoryFlight;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repositoryFlight.getFlightById(masterId);
		status = flight != null && (!flight.getIsDraft() || super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repositoryFlight.getFlightById(masterId);

		leg = new Leg();
		leg.setStatus(LegStatus.ON_TIME);
		leg.setFlight(flight);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft");
	}

	@Override
	public void validate(final Leg leg) {
		;
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(LegStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("draftMode", leg.getFlight().getIsDraft());
		dataset.put("status", choices);

		super.getResponse().addData(dataset);
	}

}
