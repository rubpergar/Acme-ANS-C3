
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
		boolean authorised = false;

		if (super.getRequest().hasData("masterId")) {
			String masterIdRaw = super.getRequest().getData("masterId", String.class);

			if (this.isPositiveInt(masterIdRaw)) {
				int masterId = Integer.parseInt(masterIdRaw);

				FlightAssignment flightAssignment = this.flightAssignmentrepository.getFlightAssignmentById(masterId);

				if (flightAssignment != null) {
					boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
					authorised = flightAssignment.isDraftMode() ? isOwner : true;
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
		Collection<ActivityLog> activityLogs;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		activityLogs = this.activityLogRepository.getActivityLogsByMasterId(masterId);

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "type", "description", "severityLevel", "draftMode");
		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLog) {
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
