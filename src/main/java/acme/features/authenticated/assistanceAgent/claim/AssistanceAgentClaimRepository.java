
package acme.features.authenticated.assistanceAgent.claim;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.legs.Leg;
import acme.entities.trackingLogs.TrackingLog;
import acme.realms.AssistanceAgent;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c WHERE c.id = :claimId")
	Claim getClaimById(@Param("claimId") int claimId);

	@Query("SELECT c.leg FROM Claim c WHERE c.id = :claimId")
	Leg getLegIsByClaimId(@Param("claimId") int claimId);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :agentId")
	Collection<Claim> findClaimsByAgent(@Param("agentId") int agentId);

	@Query("SELECT a FROM AssistanceAgent a WHERE a.id = :agentId")
	AssistanceAgent getAgentById(@Param("agentId") int agentId);

	@Query("SELECT l FROM Leg l")
	Collection<Leg> getAllLegs();

	@Query("SELECT l FROM Leg l WHERE l.isDraft = false")
	Collection<Leg> getAllPublishedLegs();

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId")
	Collection<TrackingLog> getTrackingLogByClaimId(@Param("claimId") int claimId);

	@Query("select l from Leg l where l.id = :legId")
	Optional<Leg> getLegById(int legId);

}
