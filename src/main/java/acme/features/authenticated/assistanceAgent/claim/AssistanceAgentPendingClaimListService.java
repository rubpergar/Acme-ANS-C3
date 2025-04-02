
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentPendingClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	//	@Override
	//	public void load() {
	//		Collection<Claim> claims;
	//		int assistanceAgentId;
	//
	//		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
	//		claims = this.repository.findPendingClaimsByAssistanceAgent(assistanceAgentId);
	//
	//		super.getBuffer().addData(claims);
	//	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;

		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "status", "draftMode");
		super.getResponse().addData(dataset);
	}
}
