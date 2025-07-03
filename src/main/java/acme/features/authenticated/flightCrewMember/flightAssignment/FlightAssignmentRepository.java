
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("SELECT fa from FlightAssignment fa where fa.leg.scheduledArrival < :date AND fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> getCompletedFlightAssignmentsByMemberId(int id, Date date);

	@Query("SELECT fa from FlightAssignment fa where fa.leg.scheduledArrival > :date AND fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> getUncompletedFlightAssignmentsByMemberId(int id, Date date);

	@Query("SELECT fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment getFlightAssignmentById(int id);

	@Query("SELECT l FROM Leg l WHERE l.scheduledArrival > :date AND l.isDraft = false")
	Collection<Leg> findAvailableLegs(Date date);

	@Query("SELECT fcm from FlightCrewMember fcm where fcm.id = :id")
	FlightCrewMember getMemberById(int id);

	@Query("SELECT al from ActivityLog al WHERE al.flightAssignment.id = :id")
	Collection<ActivityLog> getAllActivityLogsFromAssignmentId(int id);

	@Query("SELECT fa from FlightAssignment fa WHERE fa.leg.id = :id")
	List<FlightAssignment> getAllFlightAssignmentsByLegId(int id);

	@Query("SELECT DISTINCT fa.leg FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :id")
	List<Leg> getAllLegsByMemberId(int id);

	@Query("SELECT DISTINCT fa.leg FROM FlightAssignment fa WHERE fa.flightCrewMember.id = :memberId AND fa.leg.id != :legId")
	List<Leg> getAllLegsByMemberIdExceptSelfLeg(int memberId, int legId);

	@Query("SELECT COUNT(l) > 0 FROM Leg l WHERE l.id = :id AND l.scheduledDeparture < :date")
	boolean isLegConcluded(int id, Date date);

}
