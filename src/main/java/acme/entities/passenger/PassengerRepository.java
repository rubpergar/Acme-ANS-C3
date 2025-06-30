
package acme.entities.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface PassengerRepository extends AbstractRepository {

	@Query("select p from Passenger p where p.passportNumber = :passportNumber and p.customer.id = :customerId")
	Collection<Passenger> findPassengerByPassportNumberAndCustomer(String passportNumber, int customerId);
}
