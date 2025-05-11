
package acme.features.authenticated.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimType;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.features.authenticated.assistanceAgent.claim.AssistanceAgentClaimRepository;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		Collection<TrackingLog> tls;
		int contador = 0;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);
		tls = this.claimRepository.getTrackingLogByClaimId(masterId);
		for (TrackingLog tl : tls)
			if (tl.getResolutionPercentage() == 100)
				contador += 1;
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && contador < 2;

		if (super.getRequest().hasData("id")) {
			String tlStatus = super.getRequest().getData("status", String.class);
			if (tlStatus != null && !tlStatus.equals("0"))
				try {
					ClaimType.valueOf(tlStatus);
				} catch (IllegalArgumentException e) {
					status = false;
				}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog tl;
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.getClaimByTlId(masterId);

		tl = new TrackingLog();
		tl.setClaim(claim);
		tl.setDraftMode(true);
		tl.setLastUpdate(MomentHelper.getCurrentMoment());

		super.getBuffer().addData(tl);
	}

	@Override
	public void bind(final TrackingLog tl) {
		assert tl != null;

		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);
		tl.setClaim(claim);

		super.bindObject(tl, "stepUndergoing", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog tl) {
		assert tl != null;

		//		if (tl.getResolutionPercentage() == 100.0)
		//			assert tl.getStatus() == TrackingLogStatus.ACCEPTED || tl.getStatus() == TrackingLogStatus.REJECTED;
		//		else
		//			assert tl.getStatus() == TrackingLogStatus.PENDING;
	}

	@Override
	public void perform(final TrackingLog tl) {
		assert tl != null;

		tl.setClaim(tl.getClaim());
		tl.setLastUpdate(MomentHelper.getCurrentMoment());
		tl.setStepUndergoing(tl.getStepUndergoing());
		tl.setResolutionPercentage(tl.getResolutionPercentage());
		tl.setResolution(tl.getResolution());
		tl.setStatus(tl.getStatus());
		tl.setDraftMode(tl.isDraftMode());

		this.repository.save(tl);
	}

	@Override
	public void unbind(final TrackingLog tl) {
		assert tl != null;

		Dataset dataset;
		SelectChoices statusChoices;

		statusChoices = SelectChoices.from(TrackingLogStatus.class, tl.getStatus());

		dataset = super.unbindObject(tl, "lastUpdate", "stepUndergoing", "resolutionPercentage", "resolution");
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("draftMode", true);
		dataset.put("status", statusChoices);

		super.getResponse().addData(dataset);
	}

}
