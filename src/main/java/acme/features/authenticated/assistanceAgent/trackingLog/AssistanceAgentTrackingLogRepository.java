
package acme.features.authenticated.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(@Param("claimId") int claimId);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.id = :trackingLogId")
	TrackingLog getTlById(@Param("trackingLogId") int trackingLogId);

	@Query("SELECT tl.claim FROM TrackingLog tl WHERE tl.id = :trackingLogId")
	Claim getClaimByTlId(@Param("trackingLogId") int trackingLogId);

}
