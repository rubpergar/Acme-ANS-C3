
package acme.features.authenticated.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.getFlightById(flightId);
		Manager manager = (Manager) super.getRequest().getPrincipal().getActiveRealm();

		boolean isOwner = flight != null && super.getRequest().getPrincipal().hasRealm(manager) && super.getRequest().getPrincipal().getAccountId() == flight.getAirlineManager().getUserAccount().getId();
		boolean isDraftFlight = flight != null && flight.getIsDraft();
		boolean status = isOwner && isDraftFlight;

		if (super.getRequest().hasData("id")) {

			// Validar solo si aircraftId tiene un valor distinto de 0
			Integer aircraftId = super.getRequest().getData("aircraft", int.class);
			if (aircraftId != null && aircraftId != 0) {
				Aircraft aircraft = this.repository.findAircraftById(aircraftId);
				if (aircraft == null || aircraft.getStatus() != AircraftStatus.ACTIVE)
					status = false;
			}

			// Validar solo si departureAirportId tiene un valor distinto de 0
			Integer departureAirportId = super.getRequest().getData("departureAirport", int.class);
			if (departureAirportId != null && departureAirportId != 0) {
				Airport departureAirport = this.repository.findAirportById(departureAirportId);
				if (departureAirport == null)
					status = false;
			}

			// Validar solo si arrivalAirportId tiene un valor distinto de 0
			Integer arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
			if (arrivalAirportId != null && arrivalAirportId != 0) {
				Airport arrivalAirport = this.repository.findAirportById(arrivalAirportId);
				if (arrivalAirport == null)
					status = false;
			}

			String legStatus = super.getRequest().getData("status", String.class);
			if (legStatus != null && !legStatus.equals("0"))
				try {
					LegStatus.valueOf(legStatus);
				} catch (IllegalArgumentException e) {
					status = false;
				}

		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.repository.getFlightById(masterId);

		leg = new Leg();
		leg.setFlight(flight);
		leg.setIsDraft(true);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId;
		Aircraft aircraft;
		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);

		int departureAirportId;
		Airport departureAirport;
		departureAirportId = super.getRequest().getData("departureAirport", int.class);
		departureAirport = this.repository.findAirportById(departureAirportId);

		int arrivalAirportId;
		Airport arrivalAirport;
		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		arrivalAirport = this.repository.findAirportById(arrivalAirportId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departureAirport);
		leg.setArrivalAirport(arrivalAirport);
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;
		super.state(leg.getStatus() != null, "status", "manager.leg.error.status-required");

		boolean validStatus = leg.getStatus() == LegStatus.ON_TIME || leg.getStatus() == LegStatus.DELAYED || leg.getStatus() == LegStatus.CANCELLED || leg.getStatus() == LegStatus.LANDED;
		super.state(validStatus, "status", "manager.leg.error.invalid-status");

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getScheduledDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (scheduledDeparture != null)
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
		super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
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
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("isDraft", true);
		dataset.put("status", choices);
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraft());
		dataset.put("codeIATA", leg.getFlight().getAirlineManager().getAirline().getCodeIATA());

		if (!airports.isEmpty()) {
			departureAirportChoices = SelectChoices.from(airports, "IATAcode", leg.getDepartureAirport());
			arrivalAirportChoices = SelectChoices.from(airports, "IATAcode", leg.getArrivalAirport());
			dataset.put("departureAirports", departureAirportChoices);
			dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
			dataset.put("arrivalAirports", arrivalAirportChoices);
			dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		}

		super.getResponse().addData(dataset);
	}

}
