
package acme.features.authenticated.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<Manager, Flight> {

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
		//no deberia poder editar un vuelo que no este en borrador
		if (!flight.getIsDraft())
			super.state(flight.getIsDraft(), "*", "manager.flight.form.error.notDraft", "isDraft");
	}

	@Override
	public void load() {
		Flight flight;
		int flightId;

		flightId = this.getRequest().getData("id", int.class);
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
		//if (!flight.getIsDraft())
		//super.state(flight.getIsDraft(), "*", "manager.flight.form.error.notDraft", "isDraft");
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
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
