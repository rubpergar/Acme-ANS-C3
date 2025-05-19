
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
		assert activityLog != null;

		Date registrationMoment = activityLog.getRegistrationMoment();
		Date legFinishMoment = activityLog.getFlightAssignment().getLeg().getScheduledArrival();

		// Validación de fechas: el registro debe ser después de la llegada
		Boolean registrationAfterArrival = MomentHelper.isAfter(registrationMoment, legFinishMoment);
		super.state(context, registrationAfterArrival, "registrationMoment", "acme.validation.activity-log.invalid-moment.message");

		// Validación de publicación: solo si se intenta publicar el ActivityLog
		if (!activityLog.isDraftMode()) {
			Boolean flightAssignmentPublished = !activityLog.getFlightAssignment().isDraftMode();
			super.state(context, flightAssignmentPublished, "flightAssignment", "acme.validation.flight-assignment.unpublished-assignment.message");
		}

		return !super.hasErrors(context);
	}

}
