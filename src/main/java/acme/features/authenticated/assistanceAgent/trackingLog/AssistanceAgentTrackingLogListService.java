
package acme.features.authenticated.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.features.authenticated.assistanceAgent.claim.AssistanceAgentClaimRepository;
import acme.realms.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AssistanceAgentTrackingLogRepository	repository;

	@Autowired
	private AssistanceAgentClaimRepository			claimRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = false;
		int masterId;
		Claim claim;
		int userAccountId;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent()) && claim.getAssistanceAgent().getUserAccount().getId() == userAccountId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> tls;

		tls = this.repository.findTrackingLogsByClaimId(super.getRequest().getData("masterId", int.class));

		super.getBuffer().addData(tls);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "lastUpdate", "stepUndergoing", "resolutionPercentage", "status", "resolution", "draftMode");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLogs) {
		int masterId;
		Claim claim;
		Collection<TrackingLog> tls;
		int contador = 0;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.claimRepository.getClaimById(masterId);
		tls = this.claimRepository.getTrackingLogByClaimId(masterId);
		for (TrackingLog tl : tls)
			if (tl.getResolutionPercentage() == 100)
				contador += 1;
		showCreate = contador < 2 && super.getRequest().getPrincipal().hasRealm(claim.getAssistanceAgent());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
