
package acme.features.authenticated.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;

@GuiService
public class AdministratorAircraftListService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorAircraftRepository repository;

	// AbstractListService<Administrator, Aircraft> interface --------------


	@Override
	public void authorise() {
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().setAuthorised(hasAuthority);
	}

	@Override
	public void load() {
		Collection<Aircraft> aircrafts;
		aircrafts = this.repository.getAircrafts();
		super.getBuffer().addData(aircrafts);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;

		Dataset dataset;

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		super.getResponse().addData(dataset);
	}

}
