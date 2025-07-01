
package acme.features.any.weatherDashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Any;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.SpringHelper;
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

		List<Weather> weathers;

		if (SpringHelper.isRunningOn("production"))
			weathers = this.repository.findAllWeathers();
		else
			weathers = this.getMockedWeathers();

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

	private List<Weather> getMockedWeathers() {
		List<Weather> mocked = new ArrayList<>();

		Weather w1 = new Weather();
		w1.setId(1);
		w1.setCity("Sevilla");
		w1.setCountry("España");
		w1.setDate(MomentHelper.parse("2024-12-04 10:00", "yyyy-MM-dd HH:mm"));
		w1.setDescription("heavy rain");
		w1.setTemperature(18.0);
		w1.setWindSpeed(35.0);
		w1.setHumidity(90.0);

		Weather w2 = new Weather();
		w2.setId(2);
		w2.setCity("Madrid");
		w2.setCountry("España");
		w2.setDate(MomentHelper.parse("2024-12-05 10:00", "yyyy-MM-dd HH:mm"));
		w2.setDescription("snow");
		w2.setTemperature(30.0);
		w2.setWindSpeed(10.0);
		w2.setHumidity(40.0);

		mocked.add(w1);
		mocked.add(w2);

		return mocked;
	}

}
