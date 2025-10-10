
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

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
			if (flightAssignment != null)
				authorised = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
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
	}

	@Override
	public void validate(final ActivityLog activityLog) {
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.delete(activityLog);
	}

	//	@Override
	//	public void unbind(final ActivityLog activityLog) {
	//	}
}
