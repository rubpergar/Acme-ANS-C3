
package acme.features.authenticated.assistanceAgent.claim;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
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

	@Override
	public void load() {
		Collection<Claim> claims;
		Collection<Claim> pendingClaims;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findClaimsByAgent(assistanceAgentId);
		pendingClaims = new ArrayList<>();
		for (Claim c : claims)
			if (c.getStatus() == ClaimStatus.PENDING)
				pendingClaims.add(c);

		super.getBuffer().addData(pendingClaims);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;

		ClaimStatus status = claim.getStatus();

		dataset = super.unbindObject(claim, "registrationMoment", "type", "draftMode");
		dataset.put("status", status);
		super.getResponse().addData(dataset);
	}
}
