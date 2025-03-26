
package acme.features.authenticated.manager.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@Controller
public class ManagerLegController extends AbstractGuiController<Manager, Leg> {

	@Autowired
	protected ManagerLegListService		listService;

	@Autowired
	protected ManagerLegShowService		showService;

	@Autowired
	protected ManagerLegCreateService	createService;

	@Autowired
	protected ManagerLegDeleteService	deleteService;

	@Autowired
	protected ManagerLegUpdateService	updateService;

	@Autowired
	protected ManagerLegPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
