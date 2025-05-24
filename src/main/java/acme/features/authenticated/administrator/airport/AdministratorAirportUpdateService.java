
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
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		String airpScope = super.getRequest().getData("scope", String.class);
		if (airpScope != null && !airpScope.equals("0"))
			try {
				AirportType.valueOf(airpScope);
			} catch (IllegalArgumentException e) {
				hasAuthority = false;
			}

		super.getResponse().setAuthorised(hasAuthority);
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

		super.bindObject(airport, "name", "codeIATA", "scope", "city", "country", "web", "email", "phone");
	}

	@Override
	public void validate(final Airport airport) {

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {

		airport.setName(airport.getName());
		airport.setCodeIATA(airport.getCodeIATA());
		airport.setScope(airport.getScope());
		airport.setCity(airport.getCity());
		airport.setCountry(airport.getCountry());
		airport.setWeb(airport.getWeb());
		airport.setEmail(airport.getEmail());
		airport.setPhone(airport.getPhone());
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(AirportType.class, airport.getScope());

		dataset = super.unbindObject(airport, "name", "codeIATA", "city", "country", "web", "email", "phone");
		dataset.put("scope", choices);

		super.getResponse().addData(dataset);
	}

}
