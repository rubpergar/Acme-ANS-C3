
package acme.features.authenticated.administrator.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import acme.client.components.principals.Administrator;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.legs.Leg;
import acme.entities.weathers.Weather;

@GuiService
public class AdministratorWeatherCreateService extends AbstractGuiService<Administrator, Weather> {

	@Value("${virtualCrossing.api.key}")
	private String							apiKey;

	@Autowired
	private AdministratorWeatherRepository	repository;


	public List<Weather> loadWeatherFromAPI() {

		this.repository.deleteAllWeathers();
		List<Weather> result = new ArrayList<>();

		Set<String> weatherKeys = new HashSet<>();

		List<Leg> legs = this.repository.findAllLegs();

		for (Leg leg : legs) {
			Airport origin = leg.getDepartureAirport();
			Airport destination = leg.getArrivalAirport();

			Date departure = leg.getScheduledDeparture();
			Date arrival = leg.getScheduledArrival();

			Weather weatherAtDeparture = this.getWeatherForAirportAtDate(origin, departure);
			if (weatherAtDeparture != null) {
				String key = weatherAtDeparture.getCity().toLowerCase() + "|" + weatherAtDeparture.getCountry().toLowerCase() + "|" + departure.toString();
				if (!weatherKeys.contains(key)) {
					result.add(weatherAtDeparture);
					weatherKeys.add(key);
				}
			}

			Weather weatherAtArrival = this.getWeatherForAirportAtDate(destination, arrival);
			if (weatherAtArrival != null) {
				String key = weatherAtArrival.getCity().toLowerCase() + "|" + weatherAtArrival.getCountry().toLowerCase() + "|" + arrival.toString();
				if (!weatherKeys.contains(key)) {
					result.add(weatherAtArrival);
					weatherKeys.add(key);
				}
			}

			MomentHelper.sleep(1000);
		}

		this.repository.saveAll(result);
		return result;
	}

	private Weather getWeatherForAirportAtDate(final Airport airport, final Date date) {
		try {
			String city = this.capitalizeFirstLetter(airport.getCity());
			String country = this.capitalizeFirstLetter(airport.getCountry());
			String location = city + "," + country;

			String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);

			String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location + "/" + dateStr + "?key=" + this.apiKey + "&unitGroup=metric&include=days";

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

			if (response != null && response.getBody() != null) {
				JSONObject json = new JSONObject(response.getBody());
				JSONObject day = json.getJSONArray("days").getJSONObject(0);

				Weather weather = new Weather();
				weather.setCity(city);
				weather.setCountry(country);
				weather.setTemperature(day.getDouble("temp"));
				weather.setHumidity(day.getDouble("humidity"));
				weather.setWindSpeed(day.getDouble("windspeed"));
				weather.setDate(date);
				String description = day.optString("description", "");
				weather.setDescription(description);

				return weather;
			}

		} catch (Exception e) {
			System.out.println("Weather not found for " + airport.getCity() + " at " + date);
		}

		return null;
	}

	private String capitalizeFirstLetter(final String input) {
		if (input == null || input.isEmpty())
			return input;
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

}
