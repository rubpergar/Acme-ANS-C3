
package acme.entities.flights;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.legs.LegRepository;
import acme.realms.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "isDraft")
})
@ValidFlight
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private Boolean				isDraft;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager				airlineManager;

	// Atributos derivados ---------------------------------


	@Transient
	public Date getScheduledDeparture() {
		Date scheduledDep;
		LegRepository repo;

		repo = SpringHelper.getBean(LegRepository.class);

		scheduledDep = repo.getScheduledDeparture(this.getId()).stream().findFirst().orElse(null);
		return scheduledDep;
	}

	@Transient
	public Date getScheduledArrival() {
		Date scheduledArr;
		LegRepository repo;

		repo = SpringHelper.getBean(LegRepository.class);

		scheduledArr = repo.getScheduledArrival(this.getId()).stream().findFirst().orElse(null);
		return scheduledArr;
	}

	@Transient
	public String getOriginCity() {
		String origCity;
		LegRepository repo;

		repo = SpringHelper.getBean(LegRepository.class);

		origCity = repo.getOriginCity(this.getId()).stream().findFirst().orElse(null);
		return origCity;
	}

	@Transient
	public String getDestinationCity() {
		String destCity;
		LegRepository repo;

		repo = SpringHelper.getBean(LegRepository.class);
		destCity = repo.getDestinationCity(this.getId()).stream().findFirst().orElse(null);
		return destCity;
	}

	@Transient
	public int getNumberOfLayovers() {
		Integer numbLay;
		LegRepository repo;

		repo = SpringHelper.getBean(LegRepository.class);
		numbLay = repo.getNumberLegsByFlight(this.getId());
		return numbLay != null && numbLay > 0 ? numbLay - 1 : 0;
	}

	@Transient
	public String getFlightDistinction() {
		String originCity = this.getOriginCity();
		String destinationCity = this.getDestinationCity();

		Date scheduledDepartureDate = this.getScheduledDeparture();
		Date scheduledArrivalDate = this.getScheduledArrival();

		String scheduledDeparture = scheduledDepartureDate != null ? scheduledDepartureDate.toString() : "N/A";
		String scheduledArrival = scheduledArrivalDate != null ? scheduledArrivalDate.toString() : "N/A";

		originCity = originCity != null ? originCity : "Unknown";
		destinationCity = destinationCity != null ? destinationCity : "Unknown";

		return originCity + " (" + scheduledDeparture + ") -> " + destinationCity + " (" + scheduledArrival + ")";
	}

}
