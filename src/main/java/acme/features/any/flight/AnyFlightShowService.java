
package acme.features.any.flight;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;

@GuiService
public class AnyFlightShowService extends AbstractGuiService<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Flight flight;

		masterId = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(masterId);
		status = flight != null && !flight.getIsDraft();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {

		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "isDraft");
		List<Leg> legs = this.repository.findLegsByFlightId(flight.getId()).stream().toList();
		dataset.put("legs", !legs.isEmpty());
		if (!legs.isEmpty()) {
			dataset.put("scheduledDeparture", flight.getScheduledDeparture());
			dataset.put("scheduledArrival", flight.getScheduledArrival());
			dataset.put("originCity", flight.getOriginCity());
			dataset.put("destinationCity", flight.getDestinationCity());
			dataset.put("layovers", flight.getNumberOfLayovers());
		}
		super.getResponse().addData(dataset);
	}

}
