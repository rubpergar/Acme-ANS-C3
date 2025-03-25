
package acme.constraints;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;

@Validator
public class FlightValidator extends AbstractValidator<ValidFlight, Flight> {

	@Autowired
	private LegRepository repository;


	@Override
	protected void initialise(final ValidFlight annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Flight flight, final ConstraintValidatorContext context) {

		boolean nonOverlappingLegs = true;
		Collection<Leg> legs = new ArrayList<>(this.repository.getLegsByFlight(flight.getId()));

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

		return !super.hasErrors(context);
	}

}
