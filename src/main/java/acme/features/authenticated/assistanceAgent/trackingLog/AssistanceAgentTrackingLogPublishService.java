
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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int tlId;
		Claim claim;
		TrackingLog tl;

		tlId = super.getRequest().getData("id", int.class);
		tl = this.repository.getTlById(tlId);
		claim = this.repository.getClaimByTlId(tlId);
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && tl.isDraftMode() && !claim.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog tl;
		int tlId;

		tlId = super.getRequest().getData("id", int.class);
		tl = this.repository.getTlById(tlId);

		super.getBuffer().addData(tl);
	}

	@Override
	public void bind(final TrackingLog tl) {
		assert tl != null;

		super.bindObject(tl);
	}

	@Override
	public void validate(final TrackingLog tl) {
		assert tl != null;
		assert tl.getClaim().isDraftMode() == false;
	}

	@Override
	public void perform(final TrackingLog tl) {
		assert tl != null;
		tl.setDraftMode(false);
		this.repository.save(tl);
	}

	@Override
	public void unbind(final TrackingLog tl) {
		assert tl != null;

		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(TrackingLogStatus.class, tl.getStatus());

		dataset = super.unbindObject(tl, "lastUpdate", "stepUndergoing", "resolutionPercentage", "resolution");
		dataset.put("masterId", tl.getClaim().getId());
		dataset.put("draftMode", true);
		dataset.put("status", statusChoices);

		super.getResponse().addData(dataset);
	}
}
