
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
import acme.features.authenticated.manager.leg.ManagerLegRepository;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository	repository;

	@Autowired
	private ManagerLegRepository			legRepo;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean hasAuthority = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		if (super.getRequest().getMethod().equals("POST"))
			hasAuthority = hasAuthority && this.validatePostFields();

		super.getResponse().setAuthorised(hasAuthority);
	}

	private boolean validatePostFields() {
		return this.validateStatus() && this.validateLeg();
	}

	private boolean validateLeg() {
		Integer legId = super.getRequest().getData("selectedLeg", int.class);
		if (legId != 0) {
			Leg leg = this.legRepo.getLegById(legId);
			if (leg == null || leg.getIsDraft() || leg.getScheduledArrival().after(MomentHelper.getCurrentMoment()))
				return false;
		}
		return true;
	}

	private boolean validateStatus() {
		String claimType = super.getRequest().getData("type", String.class);
		if (!claimType.equals("0"))
			try {
				ClaimType.valueOf(claimType);
			} catch (IllegalArgumentException e) {
				return false;
			}
		return true;
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
		int legId;
		Leg leg;

		legId = super.getRequest().getData("selectedLeg", int.class);
		leg = this.repository.getLegById(legId).orElse(null);

		super.bindObject(claim, "email", "description", "type");

		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		Integer legId = super.getRequest().getData("selectedLeg", int.class);
		if (legId == 0)
			super.state(false, "selectedLeg", "javax.validation.constraints.NotNull.message");
		//		Leg leg = this.legRepo.getLegById(legId);
		//		if (leg != null)
		//			if (leg.getScheduledArrival().after(MomentHelper.getCurrentMoment()))
		//				super.state(false, "selectedLeg", "javax.validation.constraints.invalid-leg.message");
	}

	@Override
	public void perform(final Claim claim) {
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
		Dataset dataset;
		SelectChoices typeChoices;
		SelectChoices legs;

		ClaimStatus status = claim.getStatus();

		typeChoices = SelectChoices.from(ClaimType.class, claim.getType());
		legs = SelectChoices.from(this.repository.getAllPublishedLegs(MomentHelper.getCurrentMoment()), "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description");
		dataset.put("status", status);
		dataset.put("draftMode", true);
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		dataset.put("selectedLeg", legs.getSelected().getKey());
		super.getResponse().addData(dataset);
	}

}
