
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
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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

				if (isOwner && fa.isDraftMode()) {
					String method = super.getRequest().getMethod();
					if ("GET".equalsIgnoreCase(method))
						status = true;
					else
						status = this.validateRelatedEntities();
				}
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
		String leg = super.getRequest().getData("leg", String.class);
		if (leg != null && !"0".equals(leg))
			if (!this.isPositiveInt(leg))
				valid = false;
			else {
				int legId = Integer.parseInt(leg);
				boolean isCurrentLeg = false;
				if (super.getRequest().hasData("id")) {
					int assignmentId = super.getRequest().getData("id", int.class);
					if (assignmentId != 0) {
						FlightAssignment original = this.repository.getFlightAssignmentById(assignmentId);
						if (original != null && original.getLeg() != null)
							if (original.getLeg().getId() == legId)
								isCurrentLeg = true;
					}
				}
				if (!isCurrentLeg) {
					int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
					int airlineId = this.repository.getMemberById(memberId).getAirline().getId();
					Collection<Leg> availableLegs = this.repository.findAvailableLegs(MomentHelper.getCurrentMoment());

					boolean legAllowed = false;
					for (Leg l : availableLegs)
						if (l.getId() == legId) {
							int lAirlineId = l.getFlight().getAirlineManager().getAirline().getId();
							if (lAirlineId == airlineId)
								legAllowed = true;
							break;
						}
					if (!legAllowed)
						valid = false;
				}
			}

		return valid;
	}

	private boolean isPositiveInt(final String s) {
		if (s == null || s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		final String INT_MAX = "2147483647";
		if (s.length() < INT_MAX.length())
			return true;
		if (s.length() > INT_MAX.length())
			return false;
		return s.compareTo(INT_MAX) <= 0;
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

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		flightAssignment.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		flightAssignment.setDraftMode(false);
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
