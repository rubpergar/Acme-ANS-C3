
package acme.constraints;

import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.StringHelper;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineRepository;
import acme.entities.airports.AirportRepository;

@Validator
public class IATACodeValidator extends AbstractValidator<ValidIATACode, String> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AirportRepository	airportR;

	@Autowired
	private AirlineRepository	airlineR;

	private String				type;

	// ConstraintValidator interface ------------------------------------------


	@Override
	public void initialize(final ValidIATACode annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final String codeIATA, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (!StringHelper.isBlank(codeIATA))
			if (this.type.equals("airport")) {
				Collection<String> codes = this.airportR.airportCodesIATAs();
				int contador = 0;
				for (String c : codes)
					if (c.equals(codeIATA))
						contador += 1;
				if (contador > 1)
					super.state(context, false, "codeIATA", "acme.validation.codeIATA.not-unique.message");

			} else if (this.type.equals("airline")) {
				Collection<Airline> airlinesCodeIATA = this.airlineR.getAirlinesByIATA(codeIATA);
				if (airlinesCodeIATA.size() > 1)
					super.state(context, false, "codeIATA", "acme.validation.codeIATA.not-unique.message");
				else if (airlinesCodeIATA.size() < 1) {
					Collection<String> airlineCodesIATAs = this.airlineR.airlineCodesIATAs();
					for (String c : airlineCodesIATAs)
						if (c.equals(codeIATA))
							super.state(context, false, "codeIATA", "acme.validation.codeIATA.not-unique.message");
				}
			}

		result = !super.hasErrors(context);
		return result;

	}

}
