
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;

@Validator
public class BirthdayValidator extends AbstractValidator<ValidBirthday, Date> {

	@Override
	protected void initialise(final ValidBirthday annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Date date, final ConstraintValidatorContext context) {
		assert context != null;

		if (date == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		Date deadline = MomentHelper.parse("2009/03/22 14:30", "yyyy/MM/dd HH:mm");

		boolean isBirthdayCorrect = MomentHelper.isBefore(date, deadline);
		super.state(context, isBirthdayCorrect, "date", "acme.validation.birthday.invalid-date.message");

		return !super.hasErrors(context);
	}
}
