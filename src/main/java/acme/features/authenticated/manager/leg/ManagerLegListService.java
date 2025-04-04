
package acme.features.authenticated.manager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.features.authenticated.manager.flight.ManagerFlightRepository;
import acme.realms.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	flightRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.flightRepository.getFlightById(masterId);
		status = flight != null && (!flight.getIsDraft() || super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Leg> legs;

		legs = this.repository.getLegByFlightId(super.getRequest().getData("masterId", int.class));

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft");
		dataset.put("departureAirport", leg.getDepartureAirport().getIATAcode());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getIATAcode());
		dataset.put("aircraft", leg.getAircraft().getRegistrationNumber());
		super.addPayload(dataset, leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status", "isDraft", "departureAirport", "arrivalAirport", "aircraft");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Leg> legs) {
		int masterId;
		Flight flight;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		flight = this.flightRepository.getFlightById(masterId);
		showCreate = flight.getIsDraft() && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
