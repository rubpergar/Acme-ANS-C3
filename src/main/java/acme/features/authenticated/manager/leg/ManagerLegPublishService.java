
package acme.features.authenticated.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
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

		boolean nonOverlappingLegs = true;

		Collection<Leg> sortedLegs = this.legRepository.getLegsByFlight(leg.getFlight().getId());

		for (int i = 0; i < sortedLegs.size() - 1; i++) {
			Leg previousLeg = sortedLegs.stream().toList().get(i);
			Leg nextLeg = sortedLegs.stream().toList().get(i + 1);

			if (previousLeg.getScheduledArrival() != null && nextLeg.getScheduledDeparture() != null) {
				boolean validLeg = MomentHelper.isBefore(previousLeg.getScheduledArrival(), nextLeg.getScheduledDeparture());
				if (!validLeg) {
					nonOverlappingLegs = false;
					super.state(nonOverlappingLegs, "legs", "acme.validation.flight.overlapping.message");
				}
			}
		}

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
		super.getResponse().addData(dataset);
	}

}
