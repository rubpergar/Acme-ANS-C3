
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
		boolean authorised = false;

		if (super.getRequest().hasData("id"))
			try {
				int activityLogId = super.getRequest().getData("id", int.class);
				ActivityLog activityLog = this.repository.getActivityLogById(activityLogId);
				FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);

				if (activityLog != null) {
					boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
					authorised = activityLog.isDraftMode() ? isOwner : true;
				}
			} catch (Throwable error) {
				// No hacemos nada, authorised se mantiene en false
			}

		super.getResponse().setAuthorised(authorised);
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
		int id;
		String leg;

		id = super.getRequest().getData("id", int.class);

		leg = this.repository.findFlightAssignmentByActivityLogId(id).getLeg().getFlightNumber();

		dataset = super.unbindObject(activityLog, "registrationMoment", "type", "description", "severityLevel", "draftMode");
		dataset.put("masterId", activityLog.getFlightAssignment().getId());
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("leg", leg);

		super.getResponse().addData(dataset);
	}

}
