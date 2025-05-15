
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		final boolean status;
		int activityLogId;
		FlightAssignment flightAssignment;

		activityLogId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);
		status = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember()) && !flightAssignment.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.getActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		assert activityLog != null;
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		assert activityLog != null;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		assert activityLog != null;
	}
}
