
package acme.features.authenticated.customer.passenger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT c FROM Customer c WHERE c.id = :userAccountId")
	Customer findCustomerByUserAccountId(Integer userAccountId);

	Passenger findPassengerById(int id);
}
