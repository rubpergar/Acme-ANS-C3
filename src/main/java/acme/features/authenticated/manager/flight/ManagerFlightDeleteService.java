
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
		super.getResponse().setAuthorised(flight.getAirlineManager().getUserAccount().getId() == userAccountId);
		//boton desactivado si el vuelo no esta publicado (no puede aparecer en la interfaz, por eso se pone aqui porque es la url) 
		if (!flight.getIsDraft())
			super.state(flight.getIsDraft(), "*", "manager.flight.form.error.notDraft", "isDraft");  //creo que va aqui
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
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description", "isDraft");
	}

	@Override
	public void validate(final Flight flight) {   //si esta publicado no se puede borrar, creo que esto va en el authorise
		assert flight != null;
		//if (!flight.getIsDraft())
		//super.state(flight.getIsDraft(), "*", "manager.flight.form.error.notDraft", "isDraft");
	}

	@Override
	public void perform(final Flight flight) {   //borra el vuelo y todos los legs asociados
		assert flight != null;

		//lo de booking no sirve para nada porque un vuelo no tiene booking si no se ha publicado(y no se puede borrar si esta publicado)

		this.repository.getLegsByFlight(flight.getId()).forEach(leg -> {
			//pasa lo mismo que antes pero con los assigments y los logs, porque un leg no puede tener eso si el vuelo no se ha llevado a cabo
			//y no se puede llevar a cabo sin ser publicado
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
