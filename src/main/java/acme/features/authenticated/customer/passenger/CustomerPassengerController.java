
package acme.features.authenticated.customer.passenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;

@Controller
public class CustomerPassengerController extends AbstractGuiController<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerListService		listService;

	@Autowired
	protected CustomerPassengerShowService		showService;

	@Autowired
	protected CustomerPassengerCreateService	createService;

	@Autowired
	protected CustomerPassengerUpdateService	updateService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
	}

}
