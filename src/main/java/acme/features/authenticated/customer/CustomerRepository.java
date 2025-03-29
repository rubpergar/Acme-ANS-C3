
package acme.features.authenticated.customer;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.realms.Customer;

@Repository
public interface CustomerRepository extends AbstractRepository {

	Collection<Customer> findByIdentifier(String identifier);

}
