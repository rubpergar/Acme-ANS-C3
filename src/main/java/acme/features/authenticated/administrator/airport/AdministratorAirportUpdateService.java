
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
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAirportRepository repository;

	// AbstractUpdateService<Administrator, Aircraft> interface --------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airport airport;
		int airportId;

		airportId = this.getRequest().getData("id", int.class);
		airport = this.repository.getAirportById(airportId);

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		assert airport != null;
		super.bindObject(airport, "name", "IATAcode", "scope", "city", "country", "web", "email", "phone");
	}

	@Override
	public void validate(final Airport airport) {
		assert airport != null;

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		assert airport != null;

		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		assert airport != null;
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(AirportType.class, airport.getScope());

		dataset = super.unbindObject(airport, "name", "IATAcode", "city", "country", "web", "email", "phone");
		dataset.put("scope", choices);
		dataset.put("readOnly", false);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
