
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.realms.Manager;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Override
	protected void initialise(final ValidManager annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {
		assert context != null;

		String identifierNumber = manager.getIdentifierNumber();
		if (identifierNumber == null || !identifierNumber.matches("^[A-Z]{2,3}\\d{6}$"))
			super.state(context, false, "identifierNumber", "acme.validation.manager.invalid-identifier.message");

		String name = manager.getIdentity().getName().trim().substring(0, 1);
		String surname = manager.getIdentity().getSurname().trim().substring(0, 1);
		String initials = name + surname;

		boolean validIdentifier = StringHelper.startsWith(identifierNumber, initials, true);
		super.state(context, validIdentifier, "identifierNumber", "acme.validation.manager.invalid-identifier.message");

		Date dateOfBirth = manager.getDateOfBirth();
		Date present = MomentHelper.getCurrentMoment();
		long edad = MomentHelper.computeDuration(dateOfBirth, present).toDays() / 365;
		Integer yearsOfExperience = manager.getYearsOfExperience();
		if (yearsOfExperience == null || yearsOfExperience > (int) edad)
			super.state(context, false, "yearsOfExperience", "acme.validation.manager.invalid-years-of-experience.message");

		return !super.hasErrors(context);
	}
}
