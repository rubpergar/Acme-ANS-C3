
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
			return false;

		String registrationNumber = aircraft.getRegistrationNumber();

		if (registrationNumber == null)
			return false;

		Optional<Aircraft> aircraftWithSameRegistrationNumber = this.aircraftRepository.findAircraftByRegistrationNumber(registrationNumber);
		if (aircraftWithSameRegistrationNumber.isPresent() && aircraftWithSameRegistrationNumber.get().getId() != aircraft.getId()) {
			super.state(context, false, "registrationNumber", "acme.validation.aircraft.duplicate-registration-number.message");
			return false;
		}

		return true;
	}

}
