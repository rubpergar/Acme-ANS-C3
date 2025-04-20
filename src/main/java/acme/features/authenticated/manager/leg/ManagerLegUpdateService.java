
package acme.features.authenticated.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.realms.Manager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(legId);
		boolean status = leg != null && leg.getIsDraft() && super.getRequest().getPrincipal().hasRealmOfType(Manager.class) && super.getRequest().getPrincipal().getAccountId() == leg.getFlight().getAirlineManager().getUserAccount().getId();

		if (super.getRequest().getMethod().equals("POST")) {
			Integer aircraftId = super.getRequest().getData("aircraft", Integer.class);
			Aircraft aircraft = aircraftId != null ? this.repository.findAircraftById(aircraftId) : null;

			Integer arrivalAirportId = super.getRequest().getData("arrivalAirport", Integer.class);
			Airport arrivalAirport = arrivalAirportId != null ? this.repository.findAirportById(arrivalAirportId) : null;

			Integer departureAirportId = super.getRequest().getData("departureAirport", Integer.class);
			Airport departureAirport = departureAirportId != null ? this.repository.findAirportById(departureAirportId) : null;

			boolean invalidAircraft = aircraftId != null && (aircraft == null || !Objects.equals(aircraft.getAirline().getId(), leg.getFlight().getAirlineManager().getAirline().getId()));
			boolean invalidDeparture = departureAirportId != null && departureAirport == null;
			boolean invalidArrival = arrivalAirportId != null && arrivalAirport == null;

			if (invalidAircraft || invalidDeparture || invalidArrival)
				status = false;
		}

		super.getResponse().setAuthorised(status);
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

		super.state(leg.getStatus() != null, "status", "manager.leg.error.status-required");

		boolean validStatus = leg.getStatus() == LegStatus.ON_TIME || leg.getStatus() == LegStatus.DELAYED || leg.getStatus() == LegStatus.CANCELLED || leg.getStatus() == LegStatus.LANDED;
		super.state(validStatus, "status", "manager.leg.error.invalid-status");
	}

	@Override
	public void perform(final Leg leg) {
		assert leg != null;
		leg.setIsDraft(leg.getIsDraft());
		leg.setFlight(leg.getFlight());
		leg.setFlightNumber(leg.getFlightNumber());
		leg.setScheduledDeparture(leg.getScheduledDeparture());
		leg.setScheduledArrival(leg.getScheduledArrival());
		leg.setStatus(leg.getStatus());
		leg.setDepartureAirport(leg.getDepartureAirport());
		leg.setArrivalAirport(leg.getArrivalAirport());
		leg.setAircraft(leg.getAircraft());

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

		Collection<Aircraft> aircraftsActives = this.repository.findAircraftsActivesWithoutLegs(AircraftStatus.ACTIVE);
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

	@Override
	public void onSuccess() {
		PrincipalHelper.handleUpdate();
	}
}
