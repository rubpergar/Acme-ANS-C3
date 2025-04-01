
package acme.features.authenticated.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("select al from ActivityLog al WHERE al.flightAssignment.id = :id")
	Collection<ActivityLog> getActivityLogsByMasterId(int id);
}
