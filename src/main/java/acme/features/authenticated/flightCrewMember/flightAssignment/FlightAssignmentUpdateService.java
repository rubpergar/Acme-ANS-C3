
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.List;

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
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		FlightAssignment flightAssignment;
		int id;
		int userAccountId;
		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.getFlightAssignmentById(id);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		super.getResponse().setAuthorised(flightAssignment.isDraftMode() && flightAssignment.getFlightCrewMember().getUserAccount().getId() == userAccountId);
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
		assert flightAssignment != null;
		super.bindObject(flightAssignment, "duty", "status", "remarks", "leg", "flightCrewMember");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {
		assert flightAssignment != null;

		if (flightAssignment.getRemarks().length() < 1 || flightAssignment.getRemarks().length() > 255)
			super.state(false, "remarks", "acme.validation.out-1-255-range.message");

		if (flightAssignment.getFlightCrewMember() != null) {
			// Los miembros no pueden tener varios leg asignados simultáneamente
			List<Leg> legsByMember = this.repository.getAllLegsByMemberId(flightAssignment.getFlightCrewMember().getId());

			for (Leg leg : legsByMember) {
				boolean departureIncompatible = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledDeparture(), leg.getScheduledDeparture(), leg.getScheduledArrival());
				boolean arrivalIncompatible = MomentHelper.isInRange(flightAssignment.getLeg().getScheduledArrival(), leg.getScheduledDeparture(), leg.getScheduledArrival());

				if (departureIncompatible || arrivalIncompatible) {
					super.state(false, "flightCrewMember", "acme.validation.flight-assignment.incompatible-legs.message");
					break;
				}
			}

			// Solo 1 piloto y 1 co-piloto por leg
			List<FlightAssignment> flightAssignmentsInLeg = this.repository.getAllFlightAssignmentsByLegId(flightAssignment.getLeg().getId());

			boolean hasPilot = false;
			boolean hasCopilot = false;
			for (FlightAssignment fa : flightAssignmentsInLeg) {
				if (fa.getDuty().equals(FlightAssignmentDuty.PILOT))
					hasPilot = true;
				if (fa.getDuty().equals(FlightAssignmentDuty.CO_PILOT))
					hasCopilot = true;
			}

			super.state(!(flightAssignment.getDuty().equals(FlightAssignmentDuty.PILOT) && hasPilot), "duty", "acme.validation.flight-assignment.has-pilot.message");
			super.state(!(flightAssignment.getDuty().equals(FlightAssignmentDuty.CO_PILOT) && hasCopilot), "duty", "acme.validation.flight-assignment.has-copilot.message");

			// No se puede publicar una asignación con leg que ya hayan ocurrido
			boolean legConcluded = this.repository.isLegConcluded(flightAssignment.getLeg().getId());

			super.state(!legConcluded, "leg", "acme.validation.flight-assignment.leg-concluded.message");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		assert flightAssignment != null;

		flightAssignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());

		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		assert flightAssignment != null;

		Collection<Leg> legs = this.repository.findAllLegs();
		Collection<FlightCrewMember> members = this.repository.findAllAvailableMembers();

		SelectChoices status = SelectChoices.from(FlightAssignmentStatus.class, flightAssignment.getStatus());
		SelectChoices duty = SelectChoices.from(FlightAssignmentDuty.class, flightAssignment.getDuty());
		SelectChoices legChoices = SelectChoices.from(legs, "flightNumber", flightAssignment.getLeg());
		SelectChoices memberChoices = SelectChoices.from(members, "employeeCode", flightAssignment.getFlightCrewMember());

		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "status", "remarks", "draftMode");
		dataset.put("status", status);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);
		dataset.put("flightCrewMember", memberChoices.getSelected().getKey());
		dataset.put("members", memberChoices);

		super.getResponse().addData(dataset);
	}

}
