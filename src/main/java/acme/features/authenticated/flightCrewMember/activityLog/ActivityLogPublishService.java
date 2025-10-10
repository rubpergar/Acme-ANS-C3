
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
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
		boolean authorised = false;

		if (super.getRequest().hasData("id", int.class)) {
			int activityLogId = super.getRequest().getData("id", int.class);
			FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);

			if (flightAssignment != null) {
				boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
				authorised = isOwner;
			}
		}

		super.getResponse().setAuthorised(authorised);
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
		super.bindObject(activityLog, "type", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		if (activityLog.getFlightAssignment().isDraftMode())
			super.state(false, "flightAssignment", "acme.validation.flight-assignment.unpublished-assignment.message");

		if (activityLog.getType().length() < 1 || activityLog.getType().length() > 50)
			super.state(false, "type", "acme.validation.out-1-50-range.message");

		if (activityLog.getDescription().length() < 1 || activityLog.getDescription().length() > 255)
			super.state(false, "description", "acme.validation.out-1-255-range.message");
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setDraftMode(false);
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
