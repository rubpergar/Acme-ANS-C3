
package acme.features.authenticated.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.features.authenticated.assistanceAgent.claim.AssistanceAgentClaimRepository;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int tlId = super.getRequest().getData("id", int.class);
		TrackingLog tl = this.repository.getTlById(tlId);

		Claim claim = this.repository.getClaimByTlId(tlId);

		boolean hasAuthority = tl != null && tl.getDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId();

		if (super.getRequest().getMethod().equals("POST"))
			hasAuthority = hasAuthority && this.validateStatus();

		super.getResponse().setAuthorised(hasAuthority);
	}

	private boolean validateStatus() {
		if (super.getRequest().hasData("status")) {
			String tlStatus = super.getRequest().getData("status", String.class);
			if (!tlStatus.equals("0"))
				try {
					TrackingLogStatus.valueOf(tlStatus);
				} catch (IllegalArgumentException e) {
					return false;
				}
		}
		return true;
	}

	@Override
	public void load() {
		TrackingLog tl;
		int tlId;

		tlId = super.getRequest().getData("id", int.class);
		tl = this.repository.getTlById(tlId);
		tl.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(tl);
	}

	@Override
	public void bind(final TrackingLog tl) {
		super.bindObject(tl, "stepUndergoing", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog tl) {
		Claim claim = this.repository.getClaimByTlId(tl.getId());
		Collection<TrackingLog> tls = this.repository.findTrackingLogsByClaimId(claim.getId());
		int contador = 0;

		for (TrackingLog t : tls) {
			if (t.getResolutionPercentage() == 100)
				contador += 1;
			if (tl.getResolutionPercentage() != null && tl.getResolutionPercentage() == 100) {
				if (contador >= 2)
					super.state(false, "resolutionPercentage", "acme.validation.trackinglog.percentage-cant-be-100.message");
				if (t.getResolutionPercentage() == 100 && t.getStatus() != tl.getStatus() && t.getId() != tl.getId())
					super.state(false, "status", "acme.validation.trackinglog.wrong-status.message");
			}
		}
	}

	@Override
	public void perform(final TrackingLog tl) {
		tl.setClaim(tl.getClaim());
		tl.setLastUpdate(MomentHelper.getCurrentMoment());
		tl.setStepUndergoing(tl.getStepUndergoing());
		tl.setResolutionPercentage(tl.getResolutionPercentage());
		tl.setResolution(tl.getResolution());
		tl.setStatus(tl.getStatus());
		tl.setDraftMode(tl.getDraftMode());

		this.repository.save(tl);
	}

	@Override
	public void unbind(final TrackingLog tl) {
		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(TrackingLogStatus.class, tl.getStatus());

		dataset = super.unbindObject(tl, "lastUpdate", "stepUndergoing", "resolutionPercentage", "resolution");
		dataset.put("masterId", tl.getClaim().getId());
		dataset.put("draftMode", tl.getDraftMode());
		dataset.put("status", statusChoices);

		super.getResponse().addData(dataset);
	}

}
