
package acme.constraints;

import java.util.Optional;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftRepository;

@Validator
public class UniqueRegistrationNumberValidator extends AbstractValidator<ValidRegistrationNumber, Aircraft> {

	@Autowired
	private AircraftRepository aircraftRepository;


	@Override
	protected void initialise(final ValidRegistrationNumber annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Aircraft aircraft, final ConstraintValidatorContext context) {
		assert context != null;

		if (aircraft == null)
			super.state(context, false, "registrationNumber", "javax.validation.constraints.NotNull.message");

		String registrationNumber = aircraft.getRegistrationNumber();

		if (registrationNumber == null)
			super.state(context, false, "registrationNumber", "javax.validation.constraints.NotNull.message");

		Optional<Aircraft> aircraftWithSameRegistrationNumber = this.aircraftRepository.findAircraftByRegistrationNumber(registrationNumber);
		if (aircraftWithSameRegistrationNumber.isPresent() && aircraftWithSameRegistrationNumber.get().getId() != aircraft.getId())
			super.state(context, false, "registrationNumber", "acme.validation.aircraft.duplicate-registration-number.message");

		return !super.hasErrors(context);
	}

}
