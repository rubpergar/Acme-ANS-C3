
package acme.features.authenticated.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.features.authenticated.assistanceAgent.claim.AssistanceAgentClaimRepository;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		Claim claim;
		int userAccountId;

		trackingLogId = super.getRequest().getData("id", int.class);
		claim = this.repository.getClaimByTlId(trackingLogId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();

		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && claim.getAssistanceAgent().getUserAccount().getId() == userAccountId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog tl;
		int id;

		id = super.getRequest().getData("id", int.class);
		tl = this.repository.getTlById(id);

		super.getBuffer().addData(tl);
	}

	@Override
	public void unbind(final TrackingLog tl) {
		Dataset dataset;
		SelectChoices statusChoices;
		final boolean showPublish;
		Claim claim;

		claim = this.repository.getClaimByTlId(tl.getId());
		statusChoices = SelectChoices.from(TrackingLogStatus.class, tl.getStatus());

		dataset = super.unbindObject(tl, "lastUpdate", "stepUndergoing", "resolutionPercentage", "resolution");
		dataset.put("draftMode", tl.isDraftMode());
		dataset.put("status", statusChoices);

		showPublish = claim.isDraftMode() == false;

		super.getResponse().addGlobal("showPublish", showPublish);

		super.getResponse().addData(dataset);
	}

}
