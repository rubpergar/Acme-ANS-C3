
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.claimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.getClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;

		Dataset dataset;

		SelectChoices typeChoices;
		typeChoices = SelectChoices.from(claimType.class, claim.getType());

		SelectChoices legs;
		legs = SelectChoices.from(this.repository.getAllLegs(), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "status");
		Leg leg = this.repository.getLegIsByClaimId(claim.getId());
		dataset.put("draftMode", claim.isDraftMode());
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		dataset.put("selectedLeg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
