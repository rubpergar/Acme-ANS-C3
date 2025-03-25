
package acme.features.authenticated.manager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.realms.Manager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Flight flight;
		int flightId;

		flightId = super.getRequest().getData("id", int.class);
		flight = this.repository.getFlightById(flightId);

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		assert flight != null;
		flight.setAirlineManager(this.repository.getManagerById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description", "isUnpublished");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;
		if (!flight.getIsUnpublished())
			super.state(flight.getIsUnpublished(), "*", "manager.flight.form.error.notDraft", "isDraftMode");
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;

		this.repository.getBookingsByFlight(flight.getId()).forEach(booking -> {
			this.repository.deleteAll(this.repository.getBookingPassengerByBooking(booking.getId()));
			this.repository.delete(booking);
		});

		this.repository.getLegsByFlight(flight.getId()).forEach(leg -> {
			this.deleteAssignmentsAndLogs(leg);
			//this.deleteClaimsAndTrackingLogs(leg);
			this.repository.delete(leg);
		});

		this.repository.delete(flight);
	}

	private void deleteAssignmentsAndLogs(final Leg leg) {
		this.repository.getAssignmentsByLeg(leg.getId()).forEach(assignment -> {
			this.repository.deleteAll(this.repository.getActivityLogsByFlightAssignment(assignment.getId()));
			this.repository.delete(assignment);
		});
	}

	/*
	 * private void deleteClaimsAndTrackingLogs(final Leg leg) {
	 * this.repository.getClaimsByLeg(leg.getId()).forEach(claim -> {
	 * this.repository.deleteAll(this.repository.getTrackingLogsByClaim(claim.getId()));
	 * this.repository.delete(claim);
	 * });
	 * }
	 */

	@Override
	public void unbind(final Flight object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbindObject(object, "tag", "selfTransfer", "cost", "description", "isUnpublished");
		super.getResponse().addData(dataset);
	}
}
