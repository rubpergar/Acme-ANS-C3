
package acme.features.manager.leg;

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
		boolean status = false;
		int masterId = super.getRequest().getData("masterId", int.class);
		Flight flight = this.repository.getFlightById(masterId);

		if (flight != null)
			if (flight.getIsDraft() && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager())) {
				String method = super.getRequest().getMethod();
				if (method.equals("GET"))
					status = true;
				else
					status = this.validateRelatedEntities();
			}
		super.getResponse().setAuthorised(status);
	}

	private boolean validateRelatedEntities() {
		boolean valid = true;

		int departureAirportId = super.getRequest().getData("departureAirport", int.class);
		if (departureAirportId != 0) {
			Airport departureAirport = this.repository.findAirportById(departureAirportId);
			if (departureAirport == null)
				valid = false;
		}

		int arrivalAirportId = super.getRequest().getData("arrivalAirport", int.class);
		if (valid && arrivalAirportId != 0) {
			Airport arrivalAirport = this.repository.findAirportById(arrivalAirportId);
			if (arrivalAirport == null)
				valid = false;
		}

		int aircraftId = super.getRequest().getData("aircraft", int.class);
		if (aircraftId != 0) {
			Aircraft aircraft = this.repository.findAircraftById(aircraftId);
			if (aircraft == null || !AircraftStatus.ACTIVE.equals(aircraft.getStatus()))
				valid = false;
		}

		return valid;
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

		boolean validScheduledDeparture = true;
		Date scheduledDeparture = leg.getScheduledDeparture();
		if (scheduledDeparture != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();
			validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);
			super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
		}

		boolean validScheduledArrival = true;
		Date scheduledArrival = leg.getScheduledArrival();
		if (scheduledArrival != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();
			validScheduledArrival = MomentHelper.isAfter(scheduledArrival, currentMoment);
			super.state(validScheduledArrival, "scheduledArrival", "acme.validation.leg.invalid-departure.message");
		}

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

		Collection<Aircraft> aircraftsActives = this.repository.findAllAircraftsByStatus(AircraftStatus.ACTIVE);

		dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival");
		String iata = leg.getFlight().getAirlineManager().getAirline().getCodeIATA();
		if (leg.getFlightNumber() == null)
			dataset.put("flightNumber", iata);
		else
			dataset.put("flightNumber", leg.getFlightNumber());

		dataset.put("masterId", leg.getFlight().getId());
		dataset.put("isDraft", leg.getIsDraft());
		dataset.put("status", choices);
		selectedAircraft = SelectChoices.from(aircraftsActives, "registrationNumber", leg.getAircraft());
		dataset.put("aircrafts", selectedAircraft);
		dataset.put("aircraft", selectedAircraft.getSelected().getKey());
		dataset.put("isDraftFlight", leg.getFlight().getIsDraft());
		dataset.put("codeIATA", leg.getFlight().getAirlineManager().getAirline().getCodeIATA());
		departureAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "codeIATA", leg.getArrivalAirport());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("duration", leg.getDuration());

		super.getResponse().addData(dataset);
	}

}
