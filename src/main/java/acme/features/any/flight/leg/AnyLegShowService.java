
package acme.features.any.flight.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;

@GuiService
public class AnyLegShowService extends AbstractGuiService<Any, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(legId);
		status = leg != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void unbind(final Leg leg) {

		Dataset dataset;

		SelectChoices choices;
		choices = SelectChoices.from(LegStatus.class, leg.getStatus());

		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		Collection<Airport> airports;
		airports = this.repository.findAirports();

		SelectChoices selectedAircraft = new SelectChoices();

		Collection<Aircraft> aircraftsActives = this.repository.findActiveAircrafts();

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraft", leg.getIsDraft());
		dataset.put("status", choices);
		selectedAircraft = SelectChoices.from(aircraftsActives, "registrationNumber", leg.getAircraft());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("duration", leg.getDuration());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraft());
		dataset.put("codeIATA", leg.getFlight().getAirlineManager().getAirline().getCodeIATA());

		departureAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getArrivalAirport());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
