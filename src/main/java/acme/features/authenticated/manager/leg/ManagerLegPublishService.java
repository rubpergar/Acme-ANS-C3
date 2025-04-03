
package acme.features.authenticated.manager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private LegRepository			legRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId;
		Flight flight;
		Leg leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(legId);
		flight = this.repository.getFlightByLegId(legId);
		status = flight != null && flight.getIsDraft() && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) || leg.getIsDraft();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;
		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;
		/*
		 * Date scheduledDeparture = leg.getFlight().getScheduledDeparture();
		 * Date present = MomentHelper.getCurrentMoment();
		 * boolean isAfter = MomentHelper.isAfter(scheduledDeparture, present);
		 * super.state(isAfter, "*", "manager.leg.publish.error.notAfter");
		 */

	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
		leg.setIsDraft(false);  //publicado
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;

		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft", "departureAirport", "arrivalAirport", "aircraft");
		dataset.put("isDraftFlight", leg.getFlight().getIsDraft());
		super.getResponse().addData(dataset);
	}

}
