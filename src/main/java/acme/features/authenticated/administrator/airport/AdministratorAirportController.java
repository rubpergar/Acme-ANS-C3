
package acme.features.authenticated.administrator.airport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.entities.airports.Airport;

@Controller
public class AdministratorAirportController extends AbstractGuiController<Administrator, Airport> {

	@Autowired
	private AdministratorAirportListService listService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
	}

}
