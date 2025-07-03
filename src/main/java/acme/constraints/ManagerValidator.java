
package acme.constraints;

import java.util.Date;
import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.realms.Manager;
import acme.realms.ManagerRepository;

@Validator
public class ManagerValidator extends AbstractValidator<ValidManager, Manager> {

	@Autowired
	private ManagerRepository repository;


	@Override
	protected void initialise(final ValidManager annotation) {
		// Intentionally left empty
	}

	@Override
	public boolean isValid(final Manager manager, final ConstraintValidatorContext context) {

		if (manager.getIdentifierNumber() != null) {
			String identifierNumber = manager.getIdentifierNumber();

			String name = manager.getIdentity().getName().trim().substring(0, 1);
			String surname = manager.getIdentity().getSurname().trim().substring(0, 1);
			String initials = name + surname;

			boolean validIdentifier = StringHelper.startsWith(identifierNumber, initials, true);
			super.state(context, validIdentifier, "identifierNumber", "acme.validation.manager.invalid-identifier.message");
		}

		if (manager.getDateOfBirth() != null && manager.getYearsOfExperience() != null) {
			Date dateOfBirth = manager.getDateOfBirth();
			Date present = MomentHelper.getCurrentMoment();
			long edad = MomentHelper.computeDuration(dateOfBirth, present).toDays() / 365;
			Integer yearsOfExperience = manager.getYearsOfExperience();
			if (yearsOfExperience > (int) edad)
				super.state(context, false, "yearsOfExperience", "acme.validation.manager.invalid-years-of-experience.message");
		}

		String identifierNumber = manager.getIdentifierNumber();
		Optional<Manager> managersWithSameIdentifierNumber = this.repository.findManagerbyIdentifierNumber(identifierNumber);
		if (managersWithSameIdentifierNumber.isPresent() && managersWithSameIdentifierNumber.get().getId() != manager.getId())
			super.state(context, false, "identifierNumber", "acme.validation.manager.duplicate-identifier-number.message");
		return !super.hasErrors(context);
	}
}
