
package acme.features.authenticated.administrator.airport;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;

@GuiService
public class AdministratorAirportListService extends AbstractGuiService<Administrator, Airport> {
	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAirportRepository repository;

	// AbstractListService<Administrator, Aircraft> interface --------------


	@Override
	public void authorise() {
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(hasAuthority);
	}

	@Override
	public void load() {
		Collection<Airport> airports;
		airports = this.repository.findAllAirports();
		super.getBuffer().addData(airports);
	}

	@Override
	public void unbind(final Airport airport) {
		assert airport != null;

		Dataset dataset;

		dataset = super.unbindObject(airport, "name", "codeIATA", "scope", "city", "country", "web", "email", "phone");

		super.getResponse().addData(dataset);
	}

}
