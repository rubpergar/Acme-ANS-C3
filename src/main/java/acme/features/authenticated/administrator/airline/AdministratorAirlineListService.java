
package acme.features.authenticated.administrator.airline;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;

@GuiService
public class AdministratorAirlineListService extends AbstractGuiService<Administrator, Airline> {
	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAirlineRepository repository;

	// AbstractListService<Administrator, Aircraft> interface --------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Airline> airlines;
		airlines = this.repository.findAllAirlines();
		super.getBuffer().addData(airlines);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;

		Dataset dataset;

		dataset = super.unbindObject(airline, "name", "type", "foundationMoment");

		super.getResponse().addData(dataset);
	}

}
