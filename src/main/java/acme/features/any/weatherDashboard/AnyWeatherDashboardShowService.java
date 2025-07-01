
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
public class AnyWeatherDashboardShowService extends AbstractGuiService<Any, WeatherDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyWeatherDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Weather w;

		if (SpringHelper.isRunningOn("production"))
			w = this.repository.findWeatherById(id);
		else
			w = this.getMockedWeatherById(id);

		boolean status = w != null;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Weather w;

		if (SpringHelper.isRunningOn("production"))
			w = this.repository.findWeatherById(id);
		else
			w = this.getMockedWeatherById(id);

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

	private Weather getMockedWeatherById(final int id) {
		Weather weather = null;
		for (Weather w : this.getMockedWeathers())
			if (w.getId() == id)
				weather = w;
		return weather;
	}

}
