
package acme.entities.weathers;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Weather extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidNumber(min = -100, max = 100)
	@Automapped
	private Double				temperature;

	@Mandatory
	@ValidNumber(min = 0, max = 100)
	@Automapped
	private Double				humidity;

	@Mandatory
	@ValidNumber(min = 0, max = 200)
	@Automapped
	private Double				windSpeed;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				city;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				country;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				date;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

}
