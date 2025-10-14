
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
		boolean status = false;

		if (super.getRequest().hasData("id", int.class)) {
			int id = super.getRequest().getData("id", int.class);
			FlightAssignment fa = this.repository.getFlightAssignmentById(id);

			if (fa != null) {
				boolean isOwner = super.getRequest().getPrincipal().hasRealm(fa.getFlightCrewMember());

				if (isOwner && fa.isDraftMode())
					status = this.validateRelatedEntities();
			}
		}

		super.getResponse().setAuthorised(status);
	}

	private boolean validateRelatedEntities() {
		boolean valid = true;

		// Validar Duty
		String duty = super.getRequest().getData("duty", String.class);
		if (duty != null && !"0".equals(duty)) {
			boolean dutyOk = false;
			for (FlightAssignmentDuty d : FlightAssignmentDuty.values())
				if (d.name().equals(duty)) {
					dutyOk = true;
					break;
				}
			if (!dutyOk)
				valid = false;
		}

		// Validar Status
		String status = super.getRequest().getData("status", String.class);
		if (status != null && !"0".equals(status)) {
			boolean statusOk = false;
			for (FlightAssignmentStatus s : FlightAssignmentStatus.values())
				if (s.name().equals(status)) {
					statusOk = true;
					break;
				}
			if (!statusOk)
				valid = false;
		}

		// Validar Leg
		Integer leg = super.getRequest().getData("leg", Integer.class);
		if (leg != null && leg != 0) {

			boolean isCurrentLeg = false;
			Leg originalLeg = null;
			int assignmentId = super.getRequest().getData("id", int.class);
			if (assignmentId != 0) {
				FlightAssignment original = this.repository.getFlightAssignmentById(assignmentId);
				originalLeg = original.getLeg();
				if (originalLeg.getId() == leg)
					isCurrentLeg = true;
			}

			if (!isCurrentLeg) {
				int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
				int airlineId = this.repository.getMemberById(memberId).getAirline().getId();

				Collection<Leg> allAvailableLegs = this.repository.findAvailableLegs(MomentHelper.getCurrentMoment());
				List<Leg> availableLegs = allAvailableLegs.stream().filter(l -> l.getFlight().getAirlineManager().getAirline().getId() == airlineId).collect(Collectors.toList());

				List<Leg> memberAssignedLegs = this.repository.getAllLegsByMemberId(memberId);

				boolean legAllowed = false;
				for (Leg candidate : availableLegs) {
					if (candidate.getId() != leg)
						continue;

					boolean isCompatible = true;
					for (Leg assigned : memberAssignedLegs) {
						if (originalLeg != null && assigned.getId() == originalLeg.getId())
							continue;

						if (this.intervalsOverlap(candidate, assigned)) {
							isCompatible = false;
							break;
						}
					}
					if (isCompatible) {
						legAllowed = true;
						break;
					}
				}
				if (!legAllowed)
					valid = false;
			}
		}

		return valid;
	}

	private boolean intervalsOverlap(final Leg a, final Leg b) {
		return MomentHelper.isBefore(a.getScheduledDeparture(), b.getScheduledArrival()) && MomentHelper.isBefore(b.getScheduledDeparture(), a.getScheduledArrival());
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
			List<FlightAssignment> otros = this.repository.getAllFlightAssignmentsByLegId(flightAssignment.getLeg().getId()).stream().filter(x -> !x.isDraftMode()).filter(x -> x.getId() != flightAssignment.getId()).toList();

			boolean hasPilot = otros.stream().anyMatch(x -> x.getDuty() == FlightAssignmentDuty.PILOT);
			boolean hasCopilot = otros.stream().anyMatch(x -> x.getDuty() == FlightAssignmentDuty.CO_PILOT);

			super.state(!(flightAssignment.getDuty().equals(FlightAssignmentDuty.PILOT) && hasPilot), "duty", "acme.validation.flight-assignment.has-pilot.message");
			super.state(!(flightAssignment.getDuty().equals(FlightAssignmentDuty.CO_PILOT) && hasCopilot), "duty", "acme.validation.flight-assignment.has-copilot.message");

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

		for (final Leg candidate : availableLegs) {
			boolean isCompatible = true;

			for (final Leg assigned : memberAssignedLegs) {
				if (assignedLeg != null && assigned.getId() == assignedLeg.getId())
					continue;

				if (this.intervalsOverlap(candidate, assigned)) {
					isCompatible = false;
					break;
				}
			}

			if (isCompatible)
				compatibleLegs.add(candidate);
		}

		if (assignedLeg != null && compatibleLegs.stream().noneMatch(l -> l.getId() == assignedLeg.getId()))
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
