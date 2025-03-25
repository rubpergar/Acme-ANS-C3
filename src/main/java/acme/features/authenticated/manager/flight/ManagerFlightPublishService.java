
package acme.features.authenticated.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightPublishService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Flight flight;
		int flightId;
		int userAccountId;
		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(flight.getAirlineManager().getUserAccount().getId() == userAccountId); //el usuario es el manager del vuelo
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
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description", "description", "isUnpublished");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;

		Collection<Leg> legs = this.repository.getLegsByFlight(flight.getId());
		super.state(!legs.isEmpty(), "*", "manager.project.form.error.nous");

		boolean hasNullLegs = legs.stream().anyMatch(leg -> leg == null);
		super.state(!hasNullLegs, "*", "manager.flight.form.error.nullLegs");

		if (!flight.getIsUnpublished())
			super.state(false, "*", "manager.flight.form.error.notPublished", "isUnpublished");

		boolean allLegsPublished = legs.stream().allMatch(Leg::getIsUnpublished);
		super.state(!allLegsPublished, "*", "manager.flight.form.error.LegsNotPublished");
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		flight.setIsUnpublished(false);  //publicado
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;

		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "isUnpublished");
		super.getResponse().addData(dataset);
	}

}
