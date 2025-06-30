
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.flightAssignment.FlightAssignmentStatus;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.CrewAvailabilityStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().hasData("id", int.class)) {
			int id = super.getRequest().getData("id", int.class);
			FlightAssignment flightAssignment = this.repository.getFlightAssignmentById(id);
			authorised = flightAssignment.isDraftMode();
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.getFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		// No se puede publicar una asignación con un member no disponible
		if (flightAssignment.getFlightCrewMember() != null) {
			boolean availableMember = flightAssignment.getFlightCrewMember().getAvailabilityStatus() == CrewAvailabilityStatus.AVAILABLE;
			super.state(availableMember, "leg", "acme.validation.flight-assignment.unavailable-member.message");
		}

		// No se puede publicar una asignación con leg que ya hayan ocurrido
		if (flightAssignment.getLeg() != null) {
			boolean legConcluded = this.repository.isLegConcluded(flightAssignment.getLeg().getId(), MomentHelper.getCurrentMoment());
			super.state(!legConcluded, "leg", "acme.validation.flight-assignment.publish-leg-concluded.message");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setDraftMode(false);
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		int flightCrewMemberAirlineId;
		flightCrewMemberAirlineId = this.repository.getMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()).getAirline().getId();
		Collection<Leg> legs = this.repository.findAvailableLegs(flightCrewMemberAirlineId, MomentHelper.getCurrentMoment());

		Leg assignedLeg = flightAssignment.getLeg();
		if (assignedLeg != null && !legs.contains(assignedLeg))
			legs.add(assignedLeg);

		SelectChoices status = SelectChoices.from(FlightAssignmentStatus.class, flightAssignment.getStatus());
		SelectChoices duty = SelectChoices.from(FlightAssignmentDuty.class, flightAssignment.getDuty());
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", assignedLeg);

		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "status", "remarks", "draftMode");
		dataset.put("status", status);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}
}
