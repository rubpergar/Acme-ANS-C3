
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airports.Airport;
import acme.entities.airports.AirportRepository;

@Validator
public class AirportValidator extends AbstractValidator<ValidAirport, Airport> {

	@Autowired
	private AirportRepository repo;


	@Override
	protected void initialise(final ValidAirport annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;

		if (airport == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else {
			String codeIATA;
			codeIATA = airport.getCodeIATA();
			if (codeIATA == null)
				super.state(context, false, "codeIATA", "acme.validation.airport.IATA-NotNull.message");

			Collection<Airport> airports;
			airports = this.repo.findAllAirports();

			if (codeIATA != null && airports != null)
				for (Airport a : airports)
					if (a.getCodeIATA().equals(codeIATA) && a.getId() != airport.getId())
						super.state(context, false, "codeIATA", "acme.validation.aiport.codeIATA-not-unique.message");
		}

		result = !super.hasErrors(context);
		return result;

	}

}
