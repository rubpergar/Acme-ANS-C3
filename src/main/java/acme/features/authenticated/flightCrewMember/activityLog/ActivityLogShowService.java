
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		final boolean status;
		int activityLogId;
		ActivityLog activityLog;
		FlightAssignment flightAssignment;

		activityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.getActivityLogById(activityLogId);
		flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		status = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());

		if (activityLog.isDraftMode())
			super.getResponse().setAuthorised(true);
		else
			super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLogs;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLogs = this.repository.getActivityLogById(id);

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "type", "description", "severityLevel", "draftMode", "flightAssignment");
		dataset.put("masterId", activityLog.getFlightAssignment().getId());
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("flightAssignment", activityLog.getFlightAssignment().getId());

		super.getResponse().addData(dataset);
	}

}
