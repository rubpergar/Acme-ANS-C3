
package acme.features.authenticated.customer.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@Controller
public class CustomerBookingController extends AbstractGuiController<Customer, Booking> {

	@Autowired
	protected CustomerBookingListService	listService;

	@Autowired
	protected CustomerBookingShowService	showService;

	@Autowired
	protected CustomerBookingCreateService	createService;

	@Autowired
	protected CustomerBookingUpdateService	updateService;

	@Autowired
	protected CustomerBookingPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
