
package acme.features.authenticated.manager.leg;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.features.authenticated.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegShowService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	repositoryFlight;

	// AbstractGuiService interface ------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int legId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.getFlightByLegId(legId);

		status = flight != null && (!flight.getIsDraft() || super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && super.getRequest().getPrincipal().getAccountId() == flight.getAirlineManager().getUserAccount().getId());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(id);

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
		airports = this.repository.findAllAirports();

		SelectChoices selectedAircraft = new SelectChoices();
		selectedAircraft.add("0", "----", leg.getAircraft() == null);

		Collection<Aircraft> aircraftsActives = this.repository.findAircraftsActives(AircraftStatus.ACTIVE);
		Collection<Aircraft> finalAircrafts = new ArrayList<Aircraft>();
		for (Aircraft aircraft : aircraftsActives)
			if (aircraft.getAirline().getCodeIATA().equals(leg.getFlight().getAirlineManager().getAirline().getCodeIATA()))
				finalAircrafts.add(aircraft);

		for (Aircraft aircraft : finalAircrafts) {
			String key = Integer.toString(aircraft.getId());
			String label = aircraft.getRegistrationNumber();

			if (aircraft.getAirline() != null)
				label += " (" + aircraft.getAirline().getCodeIATA() + ")";

			boolean isSelected = aircraft.equals(leg.getAircraft());
			selectedAircraft.add(key, label, isSelected);
		}

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraft", leg.getIsDraft());
		dataset.put("status", choices);
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("duration", leg.getDuration());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraft());
		dataset.put("codeIATA", leg.getFlight().getAirlineManager().getAirline().getCodeIATA());

		if (!airports.isEmpty()) {
			departureAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getDepartureAirport());
			arrivalAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getArrivalAirport());
			dataset.put("departureAirports", departureAirportChoices);
			dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
			dataset.put("arrivalAirports", arrivalAirportChoices);
			dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		}

		super.getResponse().addData(dataset);
	}
}
