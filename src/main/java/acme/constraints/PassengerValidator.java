
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.passenger.Passenger;
import acme.entities.passenger.PassengerRepository;

public class PassengerValidator extends AbstractValidator<ValidPassenger, Passenger> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private PassengerRepository passengerRepository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidPassenger annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Passenger passenger, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (passenger == null) {
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
			return false;
		} else {
			String passportNumber = passenger.getPassportNumber();
			if (!StringHelper.isBlank(passportNumber)) {
				boolean validPattern = passportNumber.matches("[A-Z0-9]{6,9}$");
				super.state(context, validPattern, "passportNumber", "acme.validation.passport.number-wrong-pattern.message");

				int customerId = passenger.getCustomer().getId();
				boolean uniquePassport = this.passengerRepository.findPassengerByPassportNumberAndCustomer(passportNumber, customerId).isEmpty();

				if (!uniquePassport) {
					boolean sameAsCurrent = this.passengerRepository.findPassengerByPassportNumberAndCustomer(passportNumber, customerId).stream().anyMatch(p -> p.getId() == passenger.getId());
					super.state(context, sameAsCurrent, "passportNumber", "acme.validation.passport.number-not-unique.message");
				} else
					super.state(context, uniquePassport, "passportNumber", "acme.validation.passport.number-not-unique.message");
			}

			result = !super.hasErrors(context);
			return result;
		}
	}
}
