
package acme.features.authenticated.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

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
import acme.entities.legs.LegRepository;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private LegRepository			legRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(legId);

		boolean status = false;

		if (leg != null && leg.getIsDraft()) {
			Flight flight = leg.getFlight();
			status = flight != null && flight.getIsDraft() && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && super.getRequest().getPrincipal().getAccountId() == flight.getAirlineManager().getUserAccount().getId();
		}

		boolean validAircraft = true;
		boolean validAirport = true;

		Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
		Aircraft aircraft = aircraftId != null ? this.repository.findAircraftById(aircraftId) : null;
		boolean invalidAircraft = (aircraft == null || aircraft.getStatus() != AircraftStatus.ACTIVE || !Objects.equals(aircraft.getAirline().getId(), leg.getFlight().getAirlineManager().getAirline().getId())) && aircraftId != null;
		if (invalidAircraft)
			validAircraft = false;

		Integer departureAirportId = super.getRequest().getData("departureAirport", Integer.class);
		Airport departureAirport = departureAirportId != null ? this.repository.findAirportById(departureAirportId) : null;
		boolean invalidDepartureAirport = departureAirport == null && departureAirportId != null;
		if (invalidDepartureAirport)
			validAirport = false;
		Integer arrivalAirportId = super.getRequest().getData("arrivalAirport", Integer.class);
		Airport arrivalAirport = arrivalAirportId != null ? this.repository.findAirportById(arrivalAirportId) : null;
		boolean invalidArrivalAirport = arrivalAirport == null && arrivalAirportId != null;
		if (invalidArrivalAirport)
			validAirport = false;

		super.getResponse().setAuthorised(status && validAircraft && validAirport);
	}

	@Override
	public void load() {
		Leg leg;
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;
		int departureAirportId;
		int arrivalAirportId;
		int aircraftId;
		Airport departureAirport;
		Airport arrivalAirport;
		Aircraft aircraft;

		departureAirportId = super.getRequest().getData("departureAirport", int.class);
		arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		aircraftId = super.getRequest().getData("aircraft", int.class);
		departureAirport = this.repository.findAirportById(departureAirportId);
		arrivalAirport = this.repository.findAirportById(arrivalAirportId);
		aircraft = this.repository.findAircraftById(aircraftId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");

		leg.setDepartureAirport(departureAirport);
		leg.setArrivalAirport(arrivalAirport);
		leg.setAircraft(aircraft);
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getScheduledDeparture();
		Date currentMoment = MomentHelper.getCurrentMoment();
		if (scheduledDeparture != null)
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
		super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
		leg.setIsDraft(false);  //publicado
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
		dataset.put("status", choices);
		dataset.put("isDraft", leg.getIsDraft());
		dataset.put("masterId", leg.getFlight().getId());
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
