
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.ClaimType;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean authorised;
		int claimId;
		Claim claim;
		int userAccountId;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.getClaimById(claimId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		authorised = claim.getDraftMode() && claim.getAssistanceAgent().getUserAccount().getId() == userAccountId;

		super.getResponse().setAuthorised(authorised);
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
	public void bind(final Claim claim) {
		claim.setAssistanceAgent(this.repository.getAgentById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		super.bindObject(claim, "email", "description", "type", "status", "selectedLeg");
	}

	@Override
	public void validate(final Claim claim) {
	}

	@Override
	public void perform(final Claim claim) {

		this.repository.getTrackingLogByClaimId(claim.getId()).forEach(tl -> {
			this.repository.delete(tl);
		});

		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		SelectChoices typeChoices;
		typeChoices = SelectChoices.from(ClaimType.class, claim.getType());

		ClaimStatus status = claim.getStatus();

		SelectChoices legs;
		legs = SelectChoices.from(this.repository.getAllPublishedLegs(MomentHelper.getCurrentMoment()), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description");
		dataset.put("status", status);
		dataset.put("draftMode", claim.getDraftMode());
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		dataset.put("selectedLeg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);

	}

}
