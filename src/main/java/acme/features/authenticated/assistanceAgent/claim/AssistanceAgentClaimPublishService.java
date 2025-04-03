
package acme.features.authenticated.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentClaimRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		Claim claim;
		int claimId;
		int userAccountId;
		boolean status;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.getClaimById(claimId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		status = claim.isDraftMode() && claim.getAssistanceAgent().getUserAccount().getId() == userAccountId;

		super.getResponse().setAuthorised(status);
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
		assert claim != null;
		super.bindObject(claim, "registrationMoment", "email", "description", "type", "status", "selectedLeg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;

		//		Collection<TrackingLog> tls = this.repository.getTrackingLogByClaimId(claim.getId());
		//		super.state(!tls.isEmpty(), "*", "assistanceAgent.project.publish.error.noTrackingLogs");
		//
		//		boolean allLogsPublished = tls.stream().allMatch(TrackingLog::isDraftMode);
		//		super.state(!allLogsPublished, "*", "assistanceAgent.claim.error.notAllPublished");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		claim.setDraftMode(false);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;
		dataset = super.unbindObject(claim, "registrationMoment", "email", "description", "type", "status", "selectedLeg", "draftMode");
		super.getResponse().addData(dataset);
	}

}
