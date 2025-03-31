
package acme.features.authenticated.customer.bookingPassenger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractGuiController;
import acme.entities.booking.BookingPassenger;
import acme.realms.Customer;

@Controller
public class CustomerBookingPassengerController extends AbstractGuiController<Customer, BookingPassenger> {

	@Autowired
	protected CustomerBookingPassengerCreateService createService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("create", this.createService);
	}

}
