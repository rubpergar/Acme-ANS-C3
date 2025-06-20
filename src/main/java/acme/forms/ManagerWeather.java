
package acme.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/weather")
public class ManagerWeather {

	private final String					API_KEY			= "d2b13b6b918528e3027a1d04917cb8b7";
	private final List<ManagerDashboard>	weatherStorage	= new ArrayList<>();
	private final RestTemplate				restTemplate	= new RestTemplate();


	@GetMapping("/{city}")
	public ResponseEntity<?> getAndStoreWeather(@PathVariable final String city) {
		String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + this.API_KEY + "&units=metric";

		try {
			Map response = this.restTemplate.getForObject(url, Map.class);
			Map main = (Map) response.get("main");
			List weatherList = (List) response.get("weather");
			Map weather = (Map) weatherList.get(0);

			ManagerDashboard data = new ManagerDashboard();

			this.weatherStorage.add(data);

			return ResponseEntity.ok(data);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error al obtener el clima: " + e.getMessage());
		}
	}

	@GetMapping
	public List<ManagerDashboard> getAllManagerDashboard() {
		return this.weatherStorage;
	}
}
