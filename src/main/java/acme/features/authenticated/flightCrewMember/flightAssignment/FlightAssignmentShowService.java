
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
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised = false;

		if (super.getRequest().hasData("id")) {
			int id = super.getRequest().getData("id", int.class);
			FlightAssignment flightAssignment = this.repository.getFlightAssignmentById(id);
			if (flightAssignment != null) {
				boolean isOwner = super.getRequest().getPrincipal().hasRealm(flightAssignment.getFlightCrewMember());
				authorised = flightAssignment.isDraftMode() ? isOwner : true;
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	private boolean intervalsOverlap(final Leg a, final Leg b) {
		return MomentHelper.isBefore(a.getScheduledDeparture(), b.getScheduledArrival()) && MomentHelper.isBefore(b.getScheduledDeparture(), a.getScheduledArrival());
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
				if (assigned.getId() == assignedLeg.getId())
					continue;

				if (this.intervalsOverlap(candidate, assigned)) {
					isCompatible = false;
					break;
				}
			}

			if (isCompatible)
				compatibleLegs.add(candidate);
		}

		if (!compatibleLegs.contains(assignedLeg))
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
