
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

		if (super.getRequest().hasData("masterId"))
			try {
				int masterId = super.getRequest().getData("masterId", int.class);
				FlightAssignment flightAssignment = this.flightAssignmentrepository.getFlightAssignmentById(masterId);

				if (flightAssignment != null) {
					boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
					authorised = flightAssignment.isDraftMode() ? isOwner : true;
				}
			} catch (Throwable error) {
				// No hacemos nada, authorised se mantiene en false
			}

		super.getResponse().setAuthorised(authorised);
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
