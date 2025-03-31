
package acme.features.authenticated.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	//	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < CURRENT_TIMESTAMP")
	//	Collection<FlightAssignment> getCompletedFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival < CURRENT_TIMESTAMP AND fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> getCompletedFlightAssignmentsByMemberId(int id);

	//	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival > CURRENT_TIMESTAMP")
	//	Collection<FlightAssignment> getUncompletedFlightAssignments();

	@Query("select fa from FlightAssignment fa where fa.leg.scheduledArrival > CURRENT_TIMESTAMP AND fa.flightCrewMember.id = :id")
	Collection<FlightAssignment> getUncompletedFlightAssignmentsByMemberId(int id);

	@Query("select fa from FlightAssignment fa where fa.id = :id")
	FlightAssignment getFlightAssignmentById(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select fcm from FlightCrewMember fcm WHERE fcm.availabilityStatus = 'AVAILABLE'")
	Collection<FlightCrewMember> findAllAvailableMembers();

	@Query("select al from ActivityLog al WHERE al.flightAssignment.id = :id")
	Collection<ActivityLog> getAllActivityLogsFromAssignmentId(int id);
}
