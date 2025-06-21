
package acme.features.authenticated.manager;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Manager;

@GuiService
public class AuthenticatedManagerUpdateService extends AbstractGuiService<Authenticated, Manager> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedManagerRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Manager object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findManagerByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Manager object) {

		super.bindObject(object, "identifierNumber", "yearsOfExperience", "dateOfBirth", "pictureLink");
	}

	@Override
	public void validate(final Manager object) {

		boolean duplicatedNumber = this.repository.findManagers().stream().anyMatch(manager -> manager.getIdentifierNumber().equals(object.getIdentifierNumber()) && manager.getId() != object.getId());
		super.state(!duplicatedNumber, "identifierNumber", "authenticated.manager.form.error.duplicatedIdentifierNumber");
	}

	@Override
	public void perform(final Manager object) {

		this.repository.save(object);
	}

	@Override
	public void unbind(final Manager object) {

		Dataset dataset;
		dataset = super.unbindObject(object, "identifierNumber", "yearsOfExperience", "dateOfBirth", "pictureLink");
		SelectChoices airlineChoices;
		airlineChoices = SelectChoices.from(this.repository.findAirlines(), "codeIATA", object.getAirline());
		dataset.put("airlineChoices", airlineChoices);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
