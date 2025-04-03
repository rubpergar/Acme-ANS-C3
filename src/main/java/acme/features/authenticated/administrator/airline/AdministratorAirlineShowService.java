
package acme.features.authenticated.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineShowService extends AbstractGuiService<Administrator, Airline> {
	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Airline airline;
		int airlineId;

		airlineId = super.getRequest().getData("id", int.class);
		airline = this.repository.getAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;

		SelectChoices type;
		type = SelectChoices.from(AirlineType.class, airline.getType());

		Dataset dataset;
		dataset = super.unbindObject(airline, "name", "codeIATA", "website", "foundationMoment", "email", "phone");
		dataset.put("type", type);
		dataset.put("confirmation", false);
		dataset.put("readonly", false);

		super.getResponse().addData(dataset);
	}

}
