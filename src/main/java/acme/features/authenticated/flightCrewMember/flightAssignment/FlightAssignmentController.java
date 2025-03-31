
package acme.features.authenticated.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	@Autowired
	protected FlightAssignmentListCompletedService		listCompletedService;

	@Autowired
	protected FlightAssignmentListUncompletedService	listUncompletedService;

	@Autowired
	protected FlightAssignmentShowService				showService;

	@Autowired
	protected FlightAssignmentCreateService				createService;

	@Autowired
	protected FlightAssignmentUpdateService				updateService;

	@Autowired
	protected FlightAssignmentDeleteService				deleteService;

	@Autowired
	protected FlightAssignmentPublishService			publishService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("listCompleted", "list", this.listCompletedService);
		super.addCustomCommand("listUncompleted", "list", this.listUncompletedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
