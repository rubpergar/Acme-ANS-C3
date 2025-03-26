
package acme.features.authenticated.manager.flight.leg;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;

@Repository
public interface FlightLegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.id = :legId")
	Leg getLegById(int legId);

}
