
package acme.features.authenticated.administrator.weather;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import acme.client.repositories.AbstractRepository;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;

@Repository
public interface AdministratorWeatherRepository extends AbstractRepository {

	@Query("SELECT a FROM Airport a")
	List<Airport> findAllAirports();

	@Query("DELETE FROM Weather")
	@Modifying
	@Transactional
	void deleteAllWeathers();

	@Query("SELECT l FROM Leg l")
	List<Leg> findAllLegs();

}
