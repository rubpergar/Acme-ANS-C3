
package acme.features.authenticated.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		if (super.getRequest().getMethod().equals("POST")) {

			// TYPE
			String type = super.getRequest().getData("type", String.class);

			if (type != null && !type.equals("0"))
				try {
					AirlineType validType = AirlineType.valueOf(type);
				} catch (IllegalArgumentException ex) {
					status = false;
				}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		airline = new Airline();
		airline.setFoundationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "codeIATA", "website", "type", "foundationMoment", "email", "phone");
	}

	@Override
	public void validate(final Airline airline) {

		if (airline.getName().length() < 1 || airline.getName().length() > 50)
			super.state(false, "name", "acme.validation.out-1-50-range.message");

		Airline existing;
		existing = this.repository.getAirlineByIATA(airline.getCodeIATA());
		if (existing != null)
			super.state(false, "codeIATA", "acme.validation.duplicated-iata.message");

		if (airline.getWebsite().length() < 1 || airline.getWebsite().length() > 255)
			super.state(false, "website", "acme.validation.out-1-255-range.message");

		if (airline.getEmail().length() < 1 || airline.getEmail().length() > 255)
			super.state(false, "email", "acme.validation.out-1-255-range.message");

		if (airline.getPhone() == null)
			super.state(true, "phone", " ");

		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "codeIATA", "website", "foundationMoment", "email", "phone");
		dataset.put("type", choices);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);
	}

}
