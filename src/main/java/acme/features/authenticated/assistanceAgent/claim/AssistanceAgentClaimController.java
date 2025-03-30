
package acme.features.authenticated.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentCompletedClaimListService	listCompletedClaimsService;

	@Autowired
	private AssistanceAgentPendingClaimListService		listPendingClaimService;

	@Autowired
	private AssistanceAgentClaimShowService				showClaimService;

	@Autowired
	private AssistanceAgentClaimCreateService			createService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("completed-list", "list", this.listCompletedClaimsService);
		super.addCustomCommand("pending-list", "list", this.listPendingClaimService);
		super.addBasicCommand("show", this.showClaimService);
		super.addBasicCommand("create", this.createService);

	}

}
