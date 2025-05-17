
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activityLog.ActivityLog;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {
	// ConstraintValidator interface ------------------------------------------

	@Override
	public void initialize(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {

		Date registrationMoment = activityLog.getRegistrationMoment();
		Date legFinishMoment = activityLog.getFlightAssignment().getLeg().getScheduledArrival();

		Boolean registrationAfterArrival = MomentHelper.isAfter(registrationMoment, legFinishMoment);

		super.state(context, registrationAfterArrival, "registrationMoment", "acme.validation.activity-log.invalid-moment.message");

		Boolean flightAssignmentDraftMode = activityLog.getFlightAssignment().isDraftMode();

		super.state(context, !flightAssignmentDraftMode, "flightAssignment", "acme.validation.flight-assignment.unpublished-assignment.message");

		boolean result = !super.hasErrors(context);
		return result;
	}

}
