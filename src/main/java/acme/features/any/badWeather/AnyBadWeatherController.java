
package acme.features.any.badWeather;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flights.Flight;

@GuiController
public class AnyBadWeatherController extends AbstractGuiController<Any, Flight> {

	@Autowired
	private AnyBadWeatherFlightListService anyBadWeatherFlightListService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-under-bad-weather", "list", this.anyBadWeatherFlightListService);
	}

}
