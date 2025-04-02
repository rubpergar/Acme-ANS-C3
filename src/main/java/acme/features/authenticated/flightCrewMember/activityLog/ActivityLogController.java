
package acme.features.authenticated.flightCrewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class ActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {

	@Autowired
	protected ActivityLogListService	listService;

	@Autowired
	protected ActivityLogShowService	showService;

	@Autowired
	protected ActivityLogCreateService	createService;

	@Autowired
	protected ActivityLogUpdateService	updateService;

	@Autowired
	protected ActivityLogDeleteService	deleteService;

	@Autowired
	protected ActivityLogPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
