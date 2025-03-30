
package acme.features.authenticated.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c WHERE (c.status IN (':accepted',':rejected')) AND c.assistanceAgent.id = :agentId")
	Collection<Claim> findCompletedClaimsByAssistanceAgent(@Param("agentId") int agentId, @Param("accepted") ClaimStatus accepted, @Param("rejected") ClaimStatus rejected);

}
