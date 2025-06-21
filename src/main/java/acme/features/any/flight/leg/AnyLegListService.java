
package acme.features.any.flight.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@GuiService
public class AnyLegListService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.findFlightById(masterId);
		status = flight != null && !flight.getIsDraft();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;
		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.findFlightById(flightId);
		super.getResponse().addGlobal("masterId", flightId);
		legs = this.repository.findLegsByFlightId(flightId);
		super.getResponse().addGlobal("masterDraft", flight.getIsDraft());
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {

		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		dataset.put("departureAirport", leg.getDepartureAirport().getCodeIATA());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getCodeIATA());
		dataset.put("aircraft", leg.getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);
	}

}
