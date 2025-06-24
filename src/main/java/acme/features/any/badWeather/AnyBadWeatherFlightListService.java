
package acme.features.any.badWeather;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.weathers.Weather;
import acme.features.any.weatherDashboard.AnyWeatherDashboardRepository;

@GuiService
public class AnyBadWeatherFlightListService extends AbstractGuiService<Any, Flight> {

	@Autowired
	protected AnyWeatherDashboardRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		final Date now = MomentHelper.getCurrentMoment();
		final Date oneMonthAgo = MomentHelper.deltaFromMoment(now, -30, ChronoUnit.DAYS);

		List<Leg> legsLastMonth = this.repository.findLegsBetween(oneMonthAgo, now);
		List<Weather> weathers = this.repository.findAllWeathers();

		Map<Flight, List<Leg>> legsByFlight = legsLastMonth.stream().collect(Collectors.groupingBy(Leg::getFlight));

		List<Flight> flightsWithBadWeather = legsByFlight.entrySet().stream().filter(entry -> {
			List<Leg> legs = entry.getValue();

			// Si alguno de los legs tiene mal clima, el vuelo se considera afectado
			return legs.stream().anyMatch(leg -> {
				Airport departureAirport = leg.getDepartureAirport();
				Airport arrivalAirport = leg.getArrivalAirport();

				Date departureTime = leg.getScheduledDeparture();
				Date arrivalTime = leg.getScheduledArrival();

				return weathers.stream().anyMatch(weather -> {
					boolean isMatchingCity = weather.getCity().equalsIgnoreCase(departureAirport.getCity()) && weather.getCountry().equalsIgnoreCase(departureAirport.getCountry())
						|| weather.getCity().equalsIgnoreCase(arrivalAirport.getCity()) && weather.getCountry().equalsIgnoreCase(arrivalAirport.getCountry());

					boolean isDuringFlight = !weather.getDate().before(departureTime) && !weather.getDate().after(arrivalTime);

					boolean hasBadWeather = weather.getWindSpeed() > 65 || weather.getHumidity() > 80 || weather.getTemperature() < 0 || weather.getTemperature() > 38;

					return isMatchingCity && isDuringFlight && hasBadWeather;
				});
			});
		}).map(Map.Entry::getKey).filter(flight -> Boolean.FALSE.equals(flight.getIsDraft())).collect(Collectors.toList());

		super.getBuffer().addData(flightsWithBadWeather);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "cost", "description", "selfTransfer");
		super.getResponse().addData(dataset);
	}
}
