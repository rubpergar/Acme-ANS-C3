
package acme.features.authenticated.manager.flight.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.legs.Leg;
import acme.features.authenticated.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class FlightLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightLegRepository		repositoryLeg;

	@Autowired
	private ManagerFlightRepository	repositoryFlight;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Leg leg;
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repositoryLeg.getLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;

		leg.setFlight(this.repositoryFlight.getFlightById(super.getRequest().getData("flightId", int.class)));

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft");
	}

	@Override
	public void validate(final Leg leg) {  //dice que la validacion del duplicado es siempre en el modelo de dominio.
		assert leg != null;
	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
		this.repositoryLeg.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;
		Dataset dataset;
		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft");
		super.getResponse().addData(dataset);
	}

}
