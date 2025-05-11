
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
import acme.entities.legs.Leg;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
		;
	}

	@Override
	public void load() {
		Claim claim;

		claim = new Claim();
		claim.setAssistanceAgent(this.repository.getAgentById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		claim.setDraftMode(true);
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;
		int legId;
		Leg leg;

		legId = super.getRequest().getData("selectedLeg", int.class);
		leg = this.repository.getLegById(legId).orElse(null);
		//		claim.setAssistanceAgent(this.repository.getAgentById(super.getRequest().getPrincipal().getActiveRealm().getId()));

		super.bindObject(claim, "email", "description", "type");

		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		//assert claim.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()); //? "...linked to a leg that occurred"
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
		claim.setEmail(claim.getEmail());
		claim.setDescription(claim.getDescription());
		claim.setType(claim.getType());
		claim.setAssistanceAgent(this.repository.getAgentById(super.getRequest().getPrincipal().getActiveRealm().getId()));
		claim.setLeg(claim.getLeg());
		claim.setDraftMode(true);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;
		SelectChoices typeChoices;
		SelectChoices legs;

		ClaimStatus status = claim.getStatus();

		typeChoices = SelectChoices.from(ClaimType.class, claim.getType());
		legs = SelectChoices.from(this.repository.getAllLegs(), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description");
		dataset.put("status", status);
		dataset.put("draftMode", true);
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		dataset.put("selectedLeg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
