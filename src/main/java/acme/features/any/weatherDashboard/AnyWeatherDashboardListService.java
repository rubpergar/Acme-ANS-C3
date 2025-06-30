
package acme.features.any.weatherDashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weathers.Weather;
import acme.forms.WeatherDashboard;

@GuiService
public class AnyWeatherDashboardListService extends AbstractGuiService<Any, WeatherDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		List<WeatherDashboard> weatherDashboards = new ArrayList<>();

		List<Weather> weathers = this.repository.findAllWeathers();

		for (Weather w : weathers) {
			WeatherDashboard wD = new WeatherDashboard();
			wD.setId(w.getId());
			wD.setCity(w.getCity());
			wD.setCountry(w.getCountry());
			wD.setDate(w.getDate());
			wD.setDescription(w.getDescription());
			wD.setTemperature(w.getTemperature());
			wD.setWindSpeed(w.getWindSpeed());
			wD.setHumidity(w.getHumidity());
			weatherDashboards.add(wD);
		}

		super.getBuffer().addData(weatherDashboards);
	}

	@Override
	public void unbind(final WeatherDashboard weathersDashboard) {
		Dataset dataset = super.unbindObject(weathersDashboard, "id", "city", "country", "date");
		dataset.put("temperature", weathersDashboard.getTemperature());
		dataset.put("description", weathersDashboard.getDescription());
		dataset.put("windSpeed", weathersDashboard.getWindSpeed());
		dataset.put("humidity", weathersDashboard.getHumidity());
		super.addPayload(dataset, weathersDashboard, "description", "temperature", "windSpeed", "humidity");
		super.getResponse().addData(dataset);
	}

}
