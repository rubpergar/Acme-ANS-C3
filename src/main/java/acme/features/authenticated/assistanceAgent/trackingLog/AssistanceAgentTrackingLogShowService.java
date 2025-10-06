
package acme.features.authenticated.assistanceAgent.trackingLog;

import java.util.Collection;

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
		final boolean showUpdate;
		int contador = 0;

		statusChoices = SelectChoices.from(TrackingLogStatus.class, tl.getStatus());

		Claim claim = this.repository.getClaimByTlId(tl.getId());
		Collection<TrackingLog> tls = this.claimRepository.getAllPublishedTlsByClaimId(claim.getId());

		for (TrackingLog t : tls)
			if (t.getResolutionPercentage() == 100)
				contador += 1;

		showUpdate = contador < 2;

		dataset = super.unbindObject(tl, "lastUpdate", "stepUndergoing", "resolutionPercentage", "resolution");
		dataset.put("draftMode", tl.getDraftMode());
		dataset.put("status", statusChoices);

		super.getResponse().addGlobal("showUpdate", showUpdate);

		super.getResponse().addData(dataset);
	}

}
