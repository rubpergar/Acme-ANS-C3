
package acme.features.manager.leg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airports.Airport;
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
		boolean status = false;
		int legId = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.getLegById(legId);
		if (leg != null) {
			boolean isDraftMode = leg.getIsDraft();
			boolean isOwner = super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager());

			if (isDraftMode && isOwner) {
				String method = super.getRequest().getMethod();
				if ("GET".equals(method))
					status = true;
				else
					status = this.validateRelatedEntities();
			}
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
		int legId;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.getLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
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
		this.validateScheduledDeparture(leg);
		this.validateOverlappingLegs(leg);
		this.validateAircraftAvailability(leg);
	}

	private void validateScheduledDeparture(final Leg leg) {
		Date scheduledDeparture = leg.getScheduledDeparture();
		if (scheduledDeparture != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();

			boolean validScheduledDeparture = MomentHelper.isAfter(scheduledDeparture, currentMoment);

			super.state(validScheduledDeparture, "scheduledDeparture", "acme.validation.leg.invalid-departure.message");
		}

		Date scheduledArrival = leg.getScheduledArrival();
		if (scheduledArrival != null) {
			Date currentMoment = MomentHelper.getCurrentMoment();

			boolean validScheduledArrival = MomentHelper.isAfter(scheduledArrival, currentMoment);

			super.state(validScheduledArrival, "scheduledArrival", "acme.validation.leg.invalid-departure.message");
		}
	}

	private void validateOverlappingLegs(final Leg leg) {
		Collection<Leg> legs = this.legRepository.getLegsByFlight2(leg.getFlight().getId());
		List<Leg> legsToValidate = legs.stream().filter(l -> !l.getIsDraft()).collect(Collectors.toList());

		List<Leg> legsToValidateOverlap = new ArrayList<>(legsToValidate);
		legsToValidateOverlap.add(leg);

		List<Leg> sortedLegsOverlap = ManagerLegPublishService.sortLegsByDeparture(legsToValidateOverlap);

		boolean nonOverlappingLegs = true;
		for (int i = 0; i < sortedLegsOverlap.size() - 1; i++) {
			Leg previousLeg = sortedLegsOverlap.get(i);
			Leg nextLeg = sortedLegsOverlap.get(i + 1);

			if (!MomentHelper.isBefore(previousLeg.getScheduledArrival(), nextLeg.getScheduledDeparture()))
				nonOverlappingLegs = false;
		}

		super.state(nonOverlappingLegs, "*", "acme.validation.flight.overlapping.message");
	}

	private void validateAircraftAvailability(final Leg leg) {
		boolean validAircraft = true;
		Aircraft aircraft = leg.getAircraft();

		if (aircraft != null) {
			Date departure = leg.getScheduledDeparture();
			Date arrival = leg.getScheduledArrival();

			for (Leg l : this.repository.findAllLegs())
				if (!l.equals(leg) && aircraft.equals(l.getAircraft())) {
					Date otherDeparture = l.getScheduledDeparture();
					Date otherArrival = l.getScheduledArrival();

					boolean overlap = MomentHelper.isBeforeOrEqual(departure, otherArrival) && MomentHelper.isBeforeOrEqual(otherDeparture, arrival);

					if (overlap) {
						validAircraft = false;
						break;
					}
				}
		}

		super.state(validAircraft, "*", "acme.validation.leg.invalid-aircraft.message");
	}

	public static List<Leg> sortLegsByDeparture(final List<Leg> legs) {
		List<Leg> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Leg::getScheduledDeparture));
		return sortedLegs;
	}

	@Override
	public void perform(final Leg leg) {
		leg.setIsDraft(false);
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

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival");
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
