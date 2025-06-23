
package acme.features.manager.flight;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.flights.Flight;
import acme.realms.Manager;

@Controller
public class ManagerFlightController extends AbstractGuiController<Manager, Flight> {

	@Autowired
	protected ManagerFlightListService		listService;

	@Autowired
	protected ManagerFlightShowService		showService;

	@Autowired
	protected ManagerFlightCreateService	createService;

	@Autowired
	protected ManagerFlightDeleteService	deleteService;

	@Autowired
	protected ManagerFlightUpdateService	updateService;

	@Autowired
	protected ManagerFlightPublishService	publishService;


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
