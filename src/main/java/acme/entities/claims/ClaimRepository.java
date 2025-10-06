
package acme.entities.claims;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	Collection<TrackingLog> getTrackingLogsByClaim(@Param("claimId") int claimId);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId AND tl.draftMode = false")
	Collection<TrackingLog> getAllPublishedTlsByClaimId(@Param("claimId") int claimId);

	@Query("SELECT c.leg FROM Claim c WHERE c.id = :claimId")
	Leg getLegByClaim(@Param("claimId") int claimId);

}
