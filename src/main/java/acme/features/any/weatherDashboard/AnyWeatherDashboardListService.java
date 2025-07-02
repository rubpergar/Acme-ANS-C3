
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
		w1.setWindSpeed(80.0);
		w1.setHumidity(90.0);

		Weather w2 = new Weather();
		w2.setId(2);
		w2.setCity("Sevilla");
		w2.setCountry("España");
		w2.setDate(MomentHelper.parse("2024-12-05 10:00", "yyyy-MM-dd HH:mm"));
		w2.setDescription("snow");
		w2.setTemperature(-5.0);
		w2.setWindSpeed(60.0);
		w2.setHumidity(40.0);

		Weather w3 = new Weather();
		w3.setId(3);
		w3.setCity("lorem");
		w3.setCountry("lorem");
		w3.setDate(MomentHelper.parse("2024-12-05 11:30", "yyyy-MM-dd HH:mm"));
		w3.setDescription("snow");
		w3.setTemperature(45.0);
		w3.setWindSpeed(10.0);
		w3.setHumidity(40.0);

		Weather w4 = new Weather();
		w4.setId(4);
		w4.setCity("lorem");
		w4.setCountry("lorem");
		w4.setDate(MomentHelper.parse("2024-12-05 11:00", "yyyy-MM-dd HH:mm"));
		w4.setDescription("snow");
		w4.setTemperature(45.0);
		w4.setWindSpeed(10.0);
		w4.setHumidity(40.0);

		Weather w5 = new Weather();
		w5.setId(5);
		w5.setCity("lorem");
		w5.setCountry("lorem");
		w5.setDate(MomentHelper.parse("2024-12-05 12:00", "yyyy-MM-dd HH:mm"));
		w5.setDescription("clear sky");
		w5.setTemperature(25.0);
		w5.setWindSpeed(10.0);
		w5.setHumidity(40.0);

		Weather w6 = new Weather();
		w6.setId(6);
		w6.setCity("lorem");
		w6.setCountry("lorem");
		w6.setDate(MomentHelper.parse("2024-12-04 11:00", "yyyy-MM-dd HH:mm"));
		w6.setDescription(null);
		w6.setTemperature(18.0);
		w6.setWindSpeed(40.0);
		w6.setHumidity(60.0);

		mocked.add(w1);
		mocked.add(w2);
		mocked.add(w3);
		mocked.add(w4);
		mocked.add(w5);
		mocked.add(w6);

		return mocked;
	}

}
