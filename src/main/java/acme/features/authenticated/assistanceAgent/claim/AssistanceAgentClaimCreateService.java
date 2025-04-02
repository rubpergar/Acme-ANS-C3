
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.claimType;
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
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment()); //?

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;

		claim.setAssistanceAgent(this.repository.getAgentById(super.getRequest().getPrincipal().getActiveRealm().getId()));

		super.bindObject(claim, "email", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		//assert claim.getLeg().getScheduledArrival().before(MomentHelper.getCurrentMoment()); //? "esto tiene que ser que leg este publicado"
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
		SelectChoices legs;

		typeChoices = SelectChoices.from(claimType.class, claim.getType());
		legs = SelectChoices.from(this.repository.getAllLegs(), "flightNumber", null);

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description");
		dataset.put("draftMode", true);
		dataset.put("type", typeChoices);
		dataset.put("legs", legs);
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment()); //?
		super.getResponse().addData(dataset);
	}

}
