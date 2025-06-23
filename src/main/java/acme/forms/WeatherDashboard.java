
package acme.forms;

import java.util.Date;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDashboard extends AbstractForm {

	//API-> OPENWEATHER

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	private int					id;
	private Double				temperature;
	private Double				humidity;
	private Double				windSpeed;
	private String				city;
	private String				country;
	private Date				date;


	public static WeatherDashboard of(final Double temperature, final Double humidity, final Double windSpeed, final String city, final String country, final Date date) {
		return new WeatherDashboard(temperature, humidity, windSpeed, city, country, date);
	}

	public WeatherDashboard() {

	}

	public WeatherDashboard(final Double temperature, final Double humidity, final Double windSpeed, final String city, final String country, final Date date) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.windSpeed = windSpeed;
		this.city = city;
		this.country = country;
		this.date = date;
	}
	// Relationships ----------------------------------------------------------

	// Derived attributes -----------------------------------------------------

}
