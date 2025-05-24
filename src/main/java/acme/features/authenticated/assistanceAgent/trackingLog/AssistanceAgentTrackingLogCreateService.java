
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
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);

		boolean hasAuthority = claim != null && super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class) && super.getRequest().getPrincipal().getAccountId() == claim.getAssistanceAgent().getId();

		if (super.getRequest().getMethod().equals("POST"))
			hasAuthority = hasAuthority && this.validatePostFields();

		super.getResponse().setAuthorised(hasAuthority);
	}

	private boolean validatePostFields() {
		return this.validate100() && this.validateStatus();
	}

	private boolean validate100() {
		int masterId = super.getRequest().getData("masterId", int.class);
		int contador = 0;
		Collection<TrackingLog> tls;

		tls = this.claimRepository.getTrackingLogByClaimId(masterId);

		for (TrackingLog tl : tls)
			if (tl.getResolutionPercentage() == 100)
				contador += 1;
		if (contador >= 2)
			return false;

		return true;
	}

	private boolean validateStatus() {
		if (super.getRequest().hasData("status")) {
			String tlStatus = super.getRequest().getData("status", String.class);
			if (tlStatus != null && !tlStatus.equals("0"))
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
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);
		tl.setClaim(claim);

		super.bindObject(tl, "stepUndergoing", "resolutionPercentage", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog tl) {
		int masterId = super.getRequest().getData("masterId", int.class);
		Claim claim = this.claimRepository.getClaimById(masterId);
		Collection<TrackingLog> tls = this.repository.findTrackingLogsByClaimId(masterId);
		int contador = 0;

		for (TrackingLog t : tls) {
			if (t.getResolutionPercentage() == 100)
				contador += 1;
			if (tl.getResolutionPercentage() == 100) {
				if (contador >= 2)
					super.state(false, "resolutionPercentage", "acme.validation.trackinglog.percentage-cant-be-100.message");
				if (t.getResolutionPercentage() == 100 && t.getStatus() != tl.getStatus())
					super.state(false, "status", "acme.validation.trackinglog.wrong-status.message");
			}
		}
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
