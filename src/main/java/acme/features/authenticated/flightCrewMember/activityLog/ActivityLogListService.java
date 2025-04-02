
package acme.features.authenticated.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.features.authenticated.flightCrewMember.flightAssignment.FlightAssignmentRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository	flightAssignmentrepository;

	@Autowired
	private ActivityLogRepository		activityLogRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		final boolean status;
		FlightAssignment flightAssignment;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.flightAssignmentrepository.getFlightAssignmentById(masterId);
		status = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());

		if (!flightAssignment.isDraftMode())
			super.getResponse().setAuthorised(true);
		else
			super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		Collection<ActivityLog> activityLogs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		activityLogs = this.activityLogRepository.getActivityLogsByMasterId(masterId);

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		assert activityLog != null;

		Dataset dataset;

		dataset = super.unbindObject(activityLog, "type", "description", "severityLevel", "draftMode");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLog) {
		assert activityLog != null;

		int masterId;
		FlightAssignment flightAssignment;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.flightAssignmentrepository.getFlightAssignmentById(masterId);
		showCreate = flightAssignment.isDraftMode();

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
