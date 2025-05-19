
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;

		try {
			int activityLogId = super.getRequest().getData("id", int.class);
			FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
			authorised = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
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
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "type", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		if (activityLog.getType().length() < 1 || activityLog.getType().length() > 50)
			super.state(false, "type", "acme.validation.out-1-50-range.message");

		if (activityLog.getDescription().length() < 1 || activityLog.getDescription().length() > 255)
			super.state(false, "description", "acme.validation.out-1-255-range.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
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
