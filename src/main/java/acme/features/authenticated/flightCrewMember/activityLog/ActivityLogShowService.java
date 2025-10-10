
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

		if (super.getRequest().hasData("id")) {
			String idRaw = super.getRequest().getData("id", String.class);

			if (this.isPositiveInt(idRaw)) {
				int activityLogId = Integer.parseInt(idRaw);

				ActivityLog activityLog = this.repository.getActivityLogById(activityLogId);
				if (activityLog != null) {
					FlightAssignment flightAssignment = this.repository.findFlightAssignmentByActivityLogId(activityLogId);

					if (flightAssignment != null) {
						boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
						authorised = activityLog.isDraftMode() ? isOwner : true;
					}
				}
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	private boolean isPositiveInt(final String s) {
		if (s == null || s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		final String INT_MAX = "2147483647";
		if (s.length() < INT_MAX.length())
			return true;
		if (s.length() > INT_MAX.length())
			return false;
		return s.compareTo(INT_MAX) <= 0;
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
