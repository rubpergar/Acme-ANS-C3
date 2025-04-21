
package acme.features.authenticated.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.airports.AirportType;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {
	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAirportRepository repository;

	// AbstractListService<Administrator, Aircraft> interface -----------------


	@Override
	public void authorise() {
		int airportId = super.getRequest().getData("id", int.class);
		Airport airport = this.repository.getAirportById(airportId);
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class) && airport != null;
		super.getResponse().setAuthorised(hasAuthority);
	}

	@Override
	public void load() {
		Airport airport;
		int airportId;

		airportId = super.getRequest().getData("id", int.class);
		airport = this.repository.getAirportById(airportId);

		super.getBuffer().addData(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		assert airport != null;

		SelectChoices choices;
		choices = SelectChoices.from(AirportType.class, airport.getScope());

		Dataset dataset;
		dataset = super.unbindObject(airport, "name", "codeIATA", "city", "country", "web", "email", "phone");
		dataset.put("confirmation", false);
		dataset.put("readonly", false);
		dataset.put("scope", choices);

		super.getResponse().addData(dataset);
	}

}
