
package acme.features.authenticated.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.features.authenticated.flightCrewMember.flightAssignment.FlightAssignmentRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ActivityLogRepository		repository;

	@Autowired
	private FlightAssignmentRepository	Frepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().hasData("masterId"))
			try {
				int masterId = super.getRequest().getData("masterId", int.class);
				FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(masterId);

				if (flightAssignment != null)
					authorised = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
			} catch (Throwable error) {
				// No hacemos nada, authorised se mantiene en false
			}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int masterId;
		FlightAssignment flightAssignment;

		masterId = super.getRequest().getData("masterId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(masterId);

		activityLog = new ActivityLog();
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setDraftMode(true);
		activityLog.setFlightAssignment(flightAssignment);

		super.getBuffer().addData(activityLog);
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
		activityLog.setDraftMode(true);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		int masterId;
		String leg;

		masterId = super.getRequest().getData("masterId", int.class);

		leg = this.Frepository.getFlightAssignmentById(masterId).getLeg().getFlightNumber();

		dataset = super.unbindObject(activityLog, "type", "description", "severityLevel", "draftMode");
		dataset.put("masterId", activityLog.getFlightAssignment().getId());
		dataset.put("registrationMoment", MomentHelper.getCurrentMoment());
		dataset.put("draftMode", activityLog.isDraftMode());
		dataset.put("leg", leg);

		super.getResponse().addData(dataset);
	}

}
