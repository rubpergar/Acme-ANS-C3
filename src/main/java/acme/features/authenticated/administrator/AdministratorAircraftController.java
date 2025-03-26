
package acme.features.authenticated.administrator;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.aircrafts.Aircraft;

@Controller
public class AdministratorAircraftController extends AbstractGuiController<Administrator, Aircraft> {

	@Autowired
	protected AdministratorAircraftListService		listService;

	@Autowired
	protected AdministratorAircraftShowService		showService;

	@Autowired
	protected AdministratorAircraftCreateService	createService;

	@Autowired
	protected AdministratorAircraftDisableService	disableService;

	@Autowired
	protected AdministratorAircraftUpdateService	updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.disableService);
		super.addBasicCommand("update", this.updateService);
	}

}
