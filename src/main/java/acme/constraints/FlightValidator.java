
package acme.constraints;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.flights.Flight;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Override
	protected void initialise(final ValidFlight annotation) {

	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {

		boolean isCostCorrect = true;
		Set<String> VALID_CURRENCIES = Currency.getAvailableCurrencies().stream().map(Currency::getCurrencyCode).filter(c -> !c.equals("XXX")).collect(Collectors.toSet());
		if (flight.getCost() != null)
			if (!VALID_CURRENCIES.contains(flight.getCost().getCurrency()))
				isCostCorrect = false;

		super.state(context, isCostCorrect, "cost", "acme.validation.flight.invalid-cost.message");

		return !super.hasErrors(context);
	}
}
