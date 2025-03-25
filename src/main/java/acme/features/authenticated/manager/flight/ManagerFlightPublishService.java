
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
		//igual que antes, no ddeberia aparecer el boton de publicar si ya esta publicado
		if (!flight.getIsDraft())
			super.state(flight.getIsDraft(), "*", "manager.flight.form.error.notDraft", "isDraft");
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
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description", "isDraft");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;

		Collection<Leg> legs = this.repository.getLegsByFlight(flight.getId());
		super.state(!legs.isEmpty(), "*", "manager.project.publish.error.noLegs");

		boolean allLegsPublished = legs.stream().allMatch(Leg::getIsDraft);
		super.state(!allLegsPublished, "*", "manager.flight.publish.error.notAllPublished");

		//porque no se puede publicar un vuelo con tramos superpuestos(y aqui ya los legs no son nulos)
		//FlightValidator validator = new FlightValidator();
		//boolean validFlight = validator.isValid(flight, null);
		//super.state(validFlight, "*", "manager.flight.publish.error.overlappingLegs");
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		flight.setIsDraft(false);  //publicado
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
