
package acme.entities.claims;

import java.beans.Transient;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.entities.trackingLogs.TrackingLogStatus;
import acme.realms.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidClaim
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	// They are registered by the assistance agent
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg					leg;

	private boolean				draftMode;

	//Atributos 


	@Transient
	public ClaimStatus getStatus() {
		ClaimStatus status;
		ClaimRepository repo;
		Collection<TrackingLog> tls;
		int claimId = this.getId();

		status = ClaimStatus.PENDING;
		repo = SpringHelper.getBean(ClaimRepository.class);
		tls = repo.getTrackingLogsByClaim(claimId);

		for (TrackingLog tl : tls)
			if (tl.getStatus() == TrackingLogStatus.ACCEPTED)
				status = ClaimStatus.ACCEPTED;
			else if (tl.getStatus() == TrackingLogStatus.REJECTED)
				status = ClaimStatus.REJECTED;

		return status;
	}

}
