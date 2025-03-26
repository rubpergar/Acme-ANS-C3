
package acme.features.authenticated.manager.flight.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@Controller
public class FlightLegController extends AbstractGuiController<Manager, Leg> {

	@Autowired
	protected FlightLegShowService		showService;

	@Autowired
	protected FlightLegCreateService	createService;

	@Autowired
	protected FlightLegDeleteService	deleteService;

	@Autowired
	protected FlightLegUpdateService	updateService;

	@Autowired
	protected FlightLegPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
