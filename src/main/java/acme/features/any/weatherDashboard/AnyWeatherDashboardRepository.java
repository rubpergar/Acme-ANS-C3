
package acme.features.any.weatherDashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.legs.Leg;
import acme.entities.weathers.Weather;

@Repository
public interface AnyWeatherDashboardRepository extends AbstractRepository {

	@Query("SELECT w FROM Weather w WHERE w.city = :city AND w.country = :country")
	List<Weather> findWeathersByCityAndCountry(String city, String country);

	@Query("SELECT l FROM Leg l WHERE l.scheduledDeparture BETWEEN :start AND :end")
	List<Leg> findLegsBetween(@Param("start") Date start, @Param("end") Date end);

	@Query("SELECT w FROM Weather w")
	List<Weather> findAllWeathers();

	@Query("SELECT w FROM Weather w WHERE w.id = :id")
	Weather findWeatherById(int id);;

}
