
package acme.features.any.weatherDashboard;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.weathers.Weather;
import acme.forms.WeatherDashboard;

@GuiService
public class AnyWeatherDashboardShowService extends AbstractGuiService<Any, WeatherDashboard> {

	// Internal state ---------------------------------------------------------

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id = super.getRequest().getData("id", int.class);
		Weather w = this.repository.findWeatherById(id);
		status = w != null;
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		int id = super.getRequest().getData("id", int.class);
		Weather w = this.repository.findWeatherById(id);

		WeatherDashboard wD = new WeatherDashboard();
		wD.setCity(w.getCity());
		wD.setCountry(w.getCountry());
		wD.setTemperature(w.getTemperature());
		wD.setHumidity(w.getHumidity());
		wD.setWindSpeed(w.getWindSpeed());
		wD.setDate(w.getDate());
		wD.setDescription(w.getDescription());

		super.getBuffer().addData(wD);
	}

	@Override
	public void unbind(final WeatherDashboard weathersDashboard) {
		Dataset dataset = super.unbindObject(weathersDashboard, "id", "city", "country", "temperature", "humidity", "windSpeed", "date", "description");
		super.getResponse().addData(dataset);
	}

}
