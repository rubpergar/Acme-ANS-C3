
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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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

		int contador = 0;

		Collection<TrackingLog> tls = this.claimRepository.getAllPublishedTlsByClaimId(claim.getId());

		boolean hasAuthority = tl != null && tl.getDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getUserAccount().getId();

		for (TrackingLog t : tls)
			if (t.getResolutionPercentage() == 100)
				contador += 1;
		if (contador >= 2)
			hasAuthority = false;

		if (super.getRequest().getMethod().equals("POST"))
			hasAuthority = hasAuthority && this.validateStatus();

		super.getResponse().setAuthorised(hasAuthority);
	}

	private boolean validateStatus() {
		String tlStatus = super.getRequest().getData("status", String.class);
		if (!tlStatus.equals("0"))
			try {
				TrackingLogStatus.valueOf(tlStatus);
			} catch (IllegalArgumentException e) {
				return false;
			}
		return true;
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
		super.bindObject(tl, "stepUndergoing", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog tl) {
		boolean noChanges = true;
		TrackingLog original = this.repository.getTlById(tl.getId());

		if (tl.getStepUndergoing() != null && !tl.getStepUndergoing().equals(original.getStepUndergoing()))
			noChanges = false;
		if (tl.getResolutionPercentage() != null && !tl.getResolutionPercentage().equals(original.getResolutionPercentage()))
			noChanges = false;
		if (tl.getStatus() != null && !tl.getStatus().equals(original.getStatus()))
			noChanges = false;
		if (tl.getResolution() != null && !tl.getResolution().equals(original.getResolution()))
			noChanges = false;

		super.state(noChanges, "*", "javax.validation.constraints.mustUpdate-first.message");

		Claim claim = this.repository.getClaimByTlId(tl.getId());
		if (claim.getDraftMode())
			super.state(false, "*", "javax.validation.constraints.claim-must-be-published");

		Collection<TrackingLog> tls = this.claimRepository.getAllPublishedTlsByClaimId(claim.getId());

		for (TrackingLog t : tls)
			if (tl.getResolutionPercentage() != null) {
				if (tl.getResolutionPercentage() != 100)
					if (tl.getResolutionPercentage() <= t.getResolutionPercentage())
						super.state(false, "resolutionPercentage", "acme.validation.trackinglog.invalid-percentage.message");

				if (tl.getResolutionPercentage() == 100)
					if (tl.getStatus() != null)
						if (t.getResolutionPercentage() == 100 && t.getStatus() != tl.getStatus())
							super.state(false, "status", "acme.validation.trackinglog.wrong-status.message");
			}

		//		if (tl.getResolutionPercentage() != null && tl.getResolutionPercentage() != 100)
		//			for (TrackingLog t : tls)
		//				if (tl.getResolutionPercentage() <= t.getResolutionPercentage())
		//					super.state(false, "resolutionPercentage", "acme.validation.trackinglog.invalid-percentage.message");
		//
		//		if (tl.getResolutionPercentage() != null && tl.getResolutionPercentage() == 100) {
		//			for (TrackingLog t : tls)
		//				if (t.getResolutionPercentage() == 100)
		//					contador += 1;
		//
		//			if (contador > 1)
		//				super.state(false, "resolutionPercentage", "acme.validation.trackinglog.percentage-cant-be-100.message");
		//			else
		//				for (TrackingLog t : tls)
		//					if (t.getResolutionPercentage() == 100)
		//						if (tl.getStatus() != null && tl.getStatus() != t.getStatus())
		//							super.state(false, "status", "acme.validation.trackinglog.wrong-status.message");
		//		}

	}

	@Override
	public void perform(final TrackingLog tl) {
		tl.setDraftMode(false);
		this.repository.save(tl);
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
		dataset.put("masterId", tl.getClaim().getId());
		dataset.put("draftMode", true);
		dataset.put("status", statusChoices);

		super.getResponse().addGlobal("showUpdate", showUpdate);

		super.getResponse().addData(dataset);
	}
}
