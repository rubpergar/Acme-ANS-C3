
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.StringHelper;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogRepository;
import acme.entities.trackingLogs.TrackingLogStatus;

public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository tl;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		// HINT: trackingLog can be null
		assert context != null;

		boolean result;

		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else {

			Double resolutionPercentage = trackingLog.getResolutionPercentage();
			if (resolutionPercentage == null)
				super.state(context, false, "resolutionPercentage", "javax.validation.constraints.NotNull.message");

			TrackingLogStatus status = trackingLog.getStatus();

			Claim claim = trackingLog.getClaim();
			if (claim == null)
				super.state(context, false, "claim", "javax.validation.constraints.NotNull.message");

			List<TrackingLog> orderedTrackingLogs = this.tl.getLastTrackingLog(claim.getId());
			if (orderedTrackingLogs == null)
				super.state(context, false, "lastTrackingLog", "javax.validation.constraints.NotNull.message");

			String resolution = trackingLog.getResolution();
			if (resolution == null)
				super.state(context, false, "resolution", "javax.validation.constraints.NotNull.message");

			if (resolutionPercentage == 100 && status == TrackingLogStatus.PENDING)
				super.state(context, false, "status", "acme.validation.trackinglog.invalid-status.message");

			if (resolutionPercentage != 100 && (status == TrackingLogStatus.ACCEPTED || status == TrackingLogStatus.REJECTED))
				super.state(context, false, "status", "acme.validation.trackinglog.invalid-status.message");

			if (status != TrackingLogStatus.PENDING && StringHelper.isBlank(resolution))
				super.state(context, false, "resolution", "acme.validation.trackinglog.resolution-mandatory-if-status-not-pending.message");

			int contador = 0;

			for (TrackingLog t : orderedTrackingLogs) {
				if (t.getResolutionPercentage() == 100)
					contador += 1;
				if (t.getLastUpdate().before(trackingLog.getLastUpdate()))
					if (t.getResolutionPercentage() > trackingLog.getResolutionPercentage())
						super.state(context, false, "resolutionPercentage", "acme.validation.trackinglog.invalid-percentage.message");
			}
			if (contador > 2)
				super.state(context, false, "resolutionPercentage", "acme.validation.trackinglog.invalid-percentage.message");

		}
		result = !super.hasErrors(context);

		return result;

	}
}
