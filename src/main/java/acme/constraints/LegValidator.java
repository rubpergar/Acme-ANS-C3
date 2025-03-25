
package acme.constraints;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.StringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class LegValidator extends AbstractValidator<ValidLeg, Leg> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidLeg annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {
		assert context != null;

		if (leg == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		// La fecha de salida y llegada programada no pueden ser nulas y la fecha de salida debe ser anterior a la fecha de llegada
		boolean isScheduleCorrect = leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null && MomentHelper.isBefore(leg.getScheduledDeparture(), leg.getScheduledArrival());

		super.state(context, isScheduleCorrect, "scheduledArrival", "acme.validation.leg.invalid-scheduled-arrival.message");

		// El número de vuelo debe comenzar con el código IATA de la aerolínea
		boolean isFlightNumberCorrect = true;
		if (leg.getAircraft() != null && leg.getAircraft().getAirline() != null) {
			String airlineIATACode = leg.getAircraft().getAirline().getCodeIATA();
			if (airlineIATACode != null)
				isFlightNumberCorrect = StringHelper.startsWith(leg.getFlightNumber(), airlineIATACode, true);
		}
		super.state(context, isFlightNumberCorrect, "flightNumber", "acme.validation.leg.invalid-flight-number.message");

		// Los tramos no pueden superponerse

		boolean nonOverlappingLegs = true;
		if (isScheduleCorrect && leg.getFlight() != null) {
			Collection<Leg> legs = new ArrayList<>(this.repository.getLegsByFlight(leg.getFlight().getId()));

			for (int i = 0; i < legs.size() - 1; i++) {
				Leg previousLeg = legs.stream().toList().get(i);
				Leg nextLeg = legs.stream().toList().get(i + 1);

				if (previousLeg.getScheduledArrival() != null && nextLeg.getScheduledDeparture() != null) {
					boolean validLeg = MomentHelper.isBefore(previousLeg.getScheduledArrival(), nextLeg.getScheduledDeparture());
					if (!validLeg) {
						nonOverlappingLegs = false;
						super.state(context, false, "legs", "acme.validation.flight.overlapping.message");
					}
				}
			}
		}

		return !super.hasErrors(context);
	}
}
