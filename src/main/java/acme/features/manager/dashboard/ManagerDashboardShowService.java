
package acme.features.manager.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airports.Airport;
import acme.entities.flights.Flight;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.forms.ManagerDashboard;
import acme.realms.Manager;

@GuiService
public class ManagerDashboardShowService extends AbstractGuiService<Manager, ManagerDashboard> {

	@Autowired
	private ManagerDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Manager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Manager manager = this.repository.findAirlineManagerById(managerId);
		List<Flight> flights = new ArrayList<>(this.repository.findFlightsByAirlineManagerId(managerId));

		ManagerDashboard dashboard = new ManagerDashboard();

		dashboard.setRankingPosition(this.computeRankingPosition(manager));
		dashboard.setYearsToRetirement(this.computeYearsToRetirement(manager));
		this.setLegsRatios(dashboard, flights);
		this.setPopularAirports(dashboard, flights);
		dashboard.setNumberofLegsByStatus(this.computeLegsByStatus(flights));
		this.setFlightCostStats(dashboard, flights);

		super.getBuffer().addData(dashboard);
	}

	private int computeRankingPosition(final Manager manager) {
		List<Manager> managers = new ArrayList<>(this.repository.findAirlineManagers());
		List<Manager> sortedManagers = managers.stream().sorted(Comparator.comparing(m -> -m.getYearsOfExperience())).collect(Collectors.toList());
		return sortedManagers.indexOf(manager) + 1;
	}

	private int computeYearsToRetirement(final Manager manager) {
		Date birth = manager.getDateOfBirth();
		Date now = MomentHelper.getCurrentMoment();
		int age = (int) (MomentHelper.computeDuration(birth, now).toDays() / 365);
		return 65 - age;
	}

	private void setLegsRatios(final ManagerDashboard dashboard, final List<Flight> flights) {
		//double total = flights.size();

		long onTime = flights.stream().filter(f -> this.hasLegWithStatus(f, LegStatus.ON_TIME)).count();

		long delayed = flights.stream().filter(f -> this.hasLegWithStatus(f, LegStatus.DELAYED)).count();

		dashboard.setOnTimeDelayedLegsRatio((double) (onTime / delayed));
		//dashboard.setOnTimeLegsRatio(total == 0 ? 0 : onTime / total);
		//dashboard.setDelayedLegsRatio(total == 0 ? 0 : delayed / total);
	}

	private boolean hasLegWithStatus(final Flight flight, final LegStatus status) {
		return this.repository.findLegsByFlightId(flight.getId()).stream().anyMatch(l -> l.getStatus() == status);
	}

	private void setPopularAirports(final ManagerDashboard dashboard, final List<Flight> flights) {
		Map<Airport, Long> usage = new HashMap<>();

		for (Flight flight : flights) {
			List<Leg> legs = new ArrayList<>(this.repository.findLegsByFlightId(flight.getId()));
			if (legs.isEmpty())
				continue;

			legs.sort(Comparator.comparing(Leg::getScheduledDeparture));
			Airport dep = legs.get(0).getDepartureAirport();
			Airport arr = legs.get(legs.size() - 1).getArrivalAirport();

			usage.put(dep, usage.getOrDefault(dep, 0L) + 1);
			usage.put(arr, usage.getOrDefault(arr, 0L) + 1);
		}

		dashboard.setMostPopularAirport(usage.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null));

		dashboard.setLeastPopularAirport(usage.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null));
	}

	private Map<LegStatus, Integer> computeLegsByStatus(final List<Flight> flights) {
		Map<LegStatus, Integer> countMap = new EnumMap<>(LegStatus.class);
		for (LegStatus status : LegStatus.values())
			countMap.put(status, 0);

		for (Flight flight : flights)
			for (Leg leg : this.repository.findLegsByFlightId(flight.getId()))
				countMap.put(leg.getStatus(), countMap.get(leg.getStatus()) + 1);
		return countMap;
	}

	private void setFlightCostStats(final ManagerDashboard dashboard, final List<Flight> flights) {
		List<Flight> validFlights = flights.stream().filter(f -> f.getCost() != null && f.getCost().getAmount() >= 0).collect(Collectors.toList());

		Set<String> currencies = validFlights.stream().map(f -> f.getCost().getCurrency()).collect(Collectors.toSet());

		List<Money> avg = new ArrayList<>();
		List<Money> dev = new ArrayList<>();
		List<Money> max = new ArrayList<>();
		List<Money> min = new ArrayList<>();

		for (String currency : currencies) {
			List<Double> amounts = validFlights.stream().filter(f -> f.getCost().getCurrency().equals(currency)).map(f -> f.getCost().getAmount()).collect(Collectors.toList());

			double average = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
			double deviation = this.computeStdDev(amounts, average);
			double maximum = amounts.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
			double minimum = amounts.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);

			avg.add(this.createMoney(currency, average));
			dev.add(this.createMoney(currency, deviation));
			max.add(this.createMoney(currency, maximum));
			min.add(this.createMoney(currency, minimum));
		}

		dashboard.setAverageFlightCost(avg);
		dashboard.setDeviationFlightCost(dev);
		dashboard.setMaximumFlightCost(max);
		dashboard.setMinimumFlightCost(min);
	}

	private double computeStdDev(final List<Double> values, final double mean) {
		if (values.size() < 2)
			return 0.0;
		double variance = values.stream().mapToDouble(v -> (v - mean) * (v - mean)).average().orElse(0.0);
		return Math.sqrt(variance);
	}

	private Money createMoney(final String currency, final double amount) {
		Money money = new Money();
		money.setCurrency(currency);
		money.setAmount(amount);
		return money;
	}

	@Override
	public void unbind(final ManagerDashboard object) {
		Dataset dataset = super.unbindObject(object, "rankingPosition", "yearsToRetirement", "onTimeDelayedLegsRatio");
		dataset.put("mostPopularAirport", this.formatAirportCode(object.getMostPopularAirport()));
		dataset.put("leastPopularAirport", this.formatAirportCode(object.getLeastPopularAirport()));
		dataset.put("numberofLegsByStatus", this.formatLegStatusCounts(object.getNumberofLegsByStatus()));
		dataset.put("averageFlightCost", this.formatMoneyList(object.getAverageFlightCost()));
		dataset.put("deviationFlightCost", this.formatMoneyList(object.getDeviationFlightCost()));
		dataset.put("maximumFlightCost", this.formatMoneyList(object.getMaximumFlightCost()));
		dataset.put("minimumFlightCost", this.formatMoneyList(object.getMinimumFlightCost()));
		super.getResponse().addData(dataset);
	}

	private String formatAirportCode(final Airport airport) {
		return airport != null ? airport.getCodeIATA() : "N/A";
	}

	private String formatLegStatusCounts(final Map<LegStatus, Integer> statusMap) {
		return statusMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> entry.getKey().name() + ": " + entry.getValue()).collect(Collectors.joining(", "));
	}

	private String formatMoneyList(final List<Money> moneyList) {
		return moneyList.stream().map(m -> m.getCurrency() + " " + String.format("%.2f", m.getAmount())).collect(Collectors.joining(", "));
	}

}
