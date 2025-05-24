
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimRepository;
import acme.entities.legs.Leg;

public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClaimRepository repo;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (claim == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Leg leg = claim.getLeg();
			if (leg == null)
				super.state(context, false, "leg", "javax.validation.constraints.NotNull.message");
			else {
				if (leg.getScheduledArrival().after(MomentHelper.getCurrentMoment()))
					super.state(context, false, "leg", "acme.validation.claim.invalid-leg");
				if (leg.getIsDraft())
					super.state(context, false, "leg", "acme.validation.claim.leg-must-be-published");

			}
		}
		result = !super.hasErrors(context);

		return result;
	}

}
