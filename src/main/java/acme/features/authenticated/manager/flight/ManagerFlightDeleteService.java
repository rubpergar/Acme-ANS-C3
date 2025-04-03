
package acme.features.authenticated.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

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
		super.getResponse().setAuthorised(flight != null && flight.getIsDraft() && flight.getAirlineManager().getUserAccount().getId() == userAccountId);
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
		flight.setAirlineManager(this.repository.getManagerById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;

	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;

		this.repository.getLegsByFlight(flight.getId()).forEach(leg -> {
			this.repository.delete(leg);
		});

		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "isDraft");
		super.getResponse().addData(dataset);
	}
}
