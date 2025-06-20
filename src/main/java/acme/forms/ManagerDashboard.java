
package acme.forms;

import java.util.List;
import java.util.Map;

import acme.client.components.basis.AbstractForm;
import acme.client.components.datatypes.Money;
import acme.entities.airports.Airport;
import acme.entities.legs.LegStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerDashboard extends AbstractForm {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	private int						rankingPosition;
	private int						yearsToRetirement;

	private Double					onTimeLegsRatio;
	private Double					delayedLegsRatio;

	private Airport					mostPopularAirport;
	private Airport					leastPopularAirport;

	private int						completedLegs;
	private int						pendingLegs;
	private int						cancelledLegs;

	private Map<LegStatus, Integer>	numberofLegsByStatus;
	private List<Money>				averageFlightCost;
	private List<Money>				deviationFlightCost;
	private List<Money>				maximumFlightCost;
	private List<Money>				minimumFlightCost;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
