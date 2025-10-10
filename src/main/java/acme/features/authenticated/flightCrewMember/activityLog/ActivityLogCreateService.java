
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

		if (super.getRequest().hasData("masterId")) {
			String masterIdRaw = super.getRequest().getData("masterId", String.class);

			if (this.isPositiveInt(masterIdRaw)) {
				int masterId = Integer.parseInt(masterIdRaw);

				FlightAssignment fa = this.repository.findFlightAssignmentById(masterId);
				if (fa != null)
					authorised = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrewMember());
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
