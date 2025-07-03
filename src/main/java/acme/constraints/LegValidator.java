
package acme.constraints;

import java.util.Optional;

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
		// Intentionally left empty

	}

	@Override
	public boolean isValid(final Leg leg, final ConstraintValidatorContext context) {

		// la fecha de salida debe ser anterior a la fecha de llegada
		boolean isScheduleCorrect = true;
		if (leg.getScheduledDeparture() != null && leg.getScheduledArrival() != null)
			isScheduleCorrect = MomentHelper.isBefore(leg.getScheduledDeparture(), leg.getScheduledArrival());
		super.state(context, isScheduleCorrect, "scheduledArrival", "acme.validation.leg.invalid-scheduled-arrival.message");

		// El número de vuelo debe comenzar con el código IATA de la aerolínea
		boolean isFlightNumberCorrect = true;
		String airlineIATACode = leg.getFlight().getAirlineManager().getAirline().getCodeIATA();
		isFlightNumberCorrect = StringHelper.startsWith(leg.getFlightNumber(), airlineIATACode, true);
		super.state(context, isFlightNumberCorrect, "flightNumber", "acme.validation.leg.invalid-flight-number.message");

		boolean isValidAirport = true;
		if (leg.getDepartureAirport() != null && leg.getDepartureAirport().equals(leg.getArrivalAirport()))
			isValidAirport = false;
		super.state(context, isValidAirport, "departureAirport", "acme.validation.leg.invalid-airport.message");

		String flightNumber = leg.getFlightNumber();
		if (flightNumber != null) {
			Optional<Leg> legWithSameFlightNumber = this.repository.findLegByFlightNumber(flightNumber);
			if (legWithSameFlightNumber.isPresent() && legWithSameFlightNumber.get().getId() != leg.getId())
				super.state(context, false, "flightNumber", "acme.validation.leg.duplicate-flight-number.message");
		}
		return !super.hasErrors(context);
	}
}
