
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;

		int id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.getFlightAssignmentById(id);
		authorised = flightAssignment.isDraftMode();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		int id;
		FlightCrewMember flightCrewMember;

		id = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.getFlightAssignmentById(id);
		flightCrewMember = this.repository.getMemberById(super.getRequest().getPrincipal().getActiveRealm().getId());

		flightAssignment.setFlightCrewMember(flightCrewMember);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "status", "remarks", "leg");

		// Evitar hacking en Duty
		String dutyRaw = super.getRequest().getData("duty", String.class);

		if (!dutyRaw.equals("0"))
			try {
				FlightAssignmentDuty.valueOf(dutyRaw);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Access is not authorised");
			}

		// Evitar hacking en Status
		String statusRaw = super.getRequest().getData("status", String.class);

		if (!statusRaw.equals("0"))
			try {
				FlightAssignmentStatus.valueOf(statusRaw);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Access is not authorised");
			}

		// Evitar hacking en Leg
		String legIdRaw = super.getRequest().getData("leg", String.class);

		try {
			int legId = Integer.parseInt(legIdRaw);

			if (legId != 0) {
				int airlineId = this.repository.getMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()).getAirline().getId();

				Collection<Leg> allAvailableLegs = this.repository.findAvailableLegs(MomentHelper.getCurrentMoment());
				List<Leg> availableLegs = allAvailableLegs.stream().filter(l -> l.getFlight().getAirlineManager().getAirline().getId() == airlineId).collect(Collectors.toList());

				int assignmentId = super.getRequest().getData("id", int.class);
				FlightAssignment originalAssignment = this.repository.getFlightAssignmentById(assignmentId);
				Leg currentLeg = originalAssignment.getLeg();

				boolean isCurrentLeg = currentLeg.getId() == legId;
				boolean isInAvailableLegs = availableLegs.stream().anyMatch(l -> l.getId() == legId);

				if (!isCurrentLeg && !isInAvailableLegs)
					throw new RuntimeException("Access is not authorised");
			}

		} catch (NumberFormatException e) {
			throw new RuntimeException("Access is not authorised");
		}

	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		if (flightAssignment.getRemarks().length() < 1 || flightAssignment.getRemarks().length() > 255)
			super.state(false, "remarks", "acme.validation.out-1-255-range.message");

		// No se puede publicar una asignación con un member no disponible
		if (flightAssignment.getFlightCrewMember().getAvailabilityStatus() != CrewAvailabilityStatus.AVAILABLE)
			super.state(false, "leg", "acme.validation.flight-assignment.unavailable-member.message");

		// Solo 1 piloto y 1 co-piloto por leg
		if (flightAssignment.getLeg() != null && flightAssignment.getDuty() != null) {
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

		}

		// No se puede publicar una asignación con leg que ya hayan ocurrido
		if (flightAssignment.getLeg() != null) {
			boolean legConcluded = this.repository.isLegConcluded(flightAssignment.getLeg().getId(), MomentHelper.getCurrentMoment());
			super.state(!legConcluded, "leg", "acme.validation.flight-assignment.leg-concluded.message");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		int flightCrewMemberAirlineId;
		int flightCrewMemberId;

		flightCrewMemberAirlineId = this.repository.getMemberById(super.getRequest().getPrincipal().getActiveRealm().getId()).getAirline().getId();
		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<Leg> allAvailableLegs = this.repository.findAvailableLegs(MomentHelper.getCurrentMoment());
		List<Leg> availableLegs = allAvailableLegs.stream().filter(l -> l.getFlight().getAirlineManager().getAirline().getId() == flightCrewMemberAirlineId).collect(Collectors.toList());
		List<Leg> memberAssignedLegs = this.repository.getAllLegsByMemberId(flightCrewMemberId);
		Leg assignedLeg = flightAssignment.getLeg();
		Collection<Leg> compatibleLegs = new ArrayList<>();

		for (Leg candidate : availableLegs) {
			boolean isCompatible = true;

			for (Leg assigned : memberAssignedLegs) {
				if (assignedLeg != null && assigned.getId() == assignedLeg.getId())
					continue;

				boolean departureOverlap = MomentHelper.isInRange(candidate.getScheduledDeparture(), assigned.getScheduledDeparture(), assigned.getScheduledArrival());
				boolean arrivalOverlap = MomentHelper.isInRange(candidate.getScheduledArrival(), assigned.getScheduledDeparture(), assigned.getScheduledArrival());

				if (departureOverlap || arrivalOverlap) {
					isCompatible = false;
					break;
				}
			}

			if (isCompatible)
				compatibleLegs.add(candidate);
		}

		if (assignedLeg != null && !compatibleLegs.contains(assignedLeg))
			compatibleLegs.add(assignedLeg);

		SelectChoices status = SelectChoices.from(FlightAssignmentStatus.class, flightAssignment.getStatus());
		SelectChoices duty = SelectChoices.from(FlightAssignmentDuty.class, flightAssignment.getDuty());
		SelectChoices legChoices = SelectChoices.from(compatibleLegs, "flightNumber", assignedLeg);

		Dataset dataset = super.unbindObject(flightAssignment, "duty", "lastUpdateMoment", "status", "remarks", "draftMode");
		dataset.put("status", status);
		dataset.put("duty", duty);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

}
