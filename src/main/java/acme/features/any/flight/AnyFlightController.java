
package acme.features.any.flight;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Any;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flights.Flight;

@GuiController
public class AnyFlightController extends AbstractGuiController<Any, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AnyFlightListService	listService;

	@Autowired
	private AnyFlightShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
