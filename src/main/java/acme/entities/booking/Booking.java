
package acme.entities.booking;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidLastNibble;
import acme.entities.flights.Flight;
import acme.entities.passenger.Passenger;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "locatorCode"), @Index(columnList = "customer_id")
})
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight				flight;

	@Mandatory
	@Valid
	@ManyToOne
	private Customer			customer;

	//-------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				purchaseMoment;

	@Mandatory
	@Valid
	@Automapped
	private TravelClass			travelClass;

	@Optional
	@ValidLastNibble
	@Automapped
	private String				lastNibble;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				isDraft;

	// Atributos derivados ---------------------------------


	@Transient
	public Money getPrice() {
		Money money = new Money();
		BookingPassengerRepository bookingPassengerRepo;
		Money flightCost;
		List<Passenger> passengers;
		Double numberOfPassengers;
		Double price;

		bookingPassengerRepo = SpringHelper.getBean(BookingPassengerRepository.class);

		passengers = bookingPassengerRepo.findAllPassengersByBookingId(this.getId()).stream().toList();
		numberOfPassengers = (double) passengers.size();

		if (this.flight == null) {
			money.setAmount(0.0);
			money.setCurrency("");
		} else {
			flightCost = this.getFlight().getCost();
			price = flightCost.getAmount() * numberOfPassengers;
			money.setAmount(price);
			money.setCurrency(flightCost.getCurrency());
		}
		return money;
	}

}
