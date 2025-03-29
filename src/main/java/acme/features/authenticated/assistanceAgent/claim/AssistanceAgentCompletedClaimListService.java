
package acme.features.authenticated.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentCompletedClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {
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
		Collection<Claim> claims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findCompletedClaimsByAssistanceAgent(assistanceAgentId, ClaimStatus.ACCEPTED, ClaimStatus.REJECTED);

		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;

		Dataset dataset;

		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "status", "draftMode");
		super.getResponse().addData(dataset);
	}

}
