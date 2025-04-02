
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.claimType;
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Claim claim;
		int claimId;
		int userAccountId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.getClaimById(claimId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();

		super.getResponse().setAuthorised(claim.isDraftMode() && claim.getAssistanceAgent().getUserAccount().getId() == userAccountId);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;

		claimId = this.getRequest().getData("id", int.class);
		claim = this.repository.getClaimById(claimId);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;
		super.bindObject(claim, "email", "description", "type", "selectedLeg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		assert claim.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()); //? "...linked to a leg that occurred"
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;

		Dataset dataset;

		SelectChoices typeChoices;
		typeChoices = SelectChoices.from(claimType.class, claim.getType());

		ClaimStatus status = claim.getStatus();

		SelectChoices legs;
		legs = SelectChoices.from(this.repository.getAllLegs(), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description");
		Leg leg = this.repository.getLegIsByClaimId(claim.getId());
		dataset.put("status", status);
		dataset.put("draftMode", claim.isDraftMode());
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		dataset.put("selectedLeg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
