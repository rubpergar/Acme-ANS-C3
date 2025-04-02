
package acme.features.authenticated.manager.flight;

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
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository	repository;

	@Autowired
	private LegRepository			legRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Flight flight;
		int flightId;
		int userAccountId;
		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(flight.getIsDraft() && flight.getAirlineManager().getUserAccount().getId() == userAccountId); //el usuario es el manager del vuelo
	}

	@Override
	public void load() {
		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		assert flight != null;
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;

		Collection<Leg> legs = this.repository.getLegsByFlight(flight.getId());
		super.state(!legs.isEmpty(), "*", "manager.project.publish.error.noLegs");

		boolean allLegsPublished = legs.stream().allMatch(Leg::getIsDraft);
		super.state(!allLegsPublished, "*", "manager.flight.publish.error.notAllPublished");

	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		flight.setIsDraft(false);  //publicado

		boolean nonOverlappingLegs = true;

		Collection<Leg> sortedLegs = this.legRepository.getLegsByFlight(flight.getId());

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
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;

		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "isDraft");
		super.getResponse().addData(dataset);
	}

}
