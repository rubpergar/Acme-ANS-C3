
package acme.entities.weathers;

/*
 * @GuiService
 * public class WeatherService {
 * 
 * @Value("${openweather.api.key}")
 * private String apiKey;
 * 
 * @Autowired
 * private WeatherRepository repository;
 * 
 * 
 * public List<Weather> loadWeatherFromAPI() {
 * this.repository.deleteAll();
 * 
 * List<Weather> result = new ArrayList<>();
 * Collection<Flight> flights = this.repository.findAllFlights();
 * 
 * for (Flight flight : flights) {
 * String city = flight.getOriginCity();
 * 
 * try {
 * String url = "https://api.openweathermap.org/data/2.5/weather?q={0}&appid={1}&units=metric";
 * url = url.replace("{0}", city).replace("{1}", this.apiKey);
 * 
 * RestTemplate restTemplate = new RestTemplate();
 * ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
 * MomentHelper.sleep(1000);
 * 
 * if (response != null && response.getBody() != null) {
 * JSONObject json = new JSONObject(response.getBody());
 * 
 * double temp = json.getJSONObject("main").getDouble("temp");
 * double humidity = json.getJSONObject("main").getDouble("humidity");
 * double windSpeed = json.getJSONObject("wind").getDouble("speed");
 * 
 * Weather weather = new Weather();
 * weather.setCity(city);
 * weather.setTemperature(temp);
 * weather.setHumidity(humidity);
 * weather.setWindSpeed(windSpeed);
 * weather.setDate(new Date());
 * weather.setFlight(flight);
 * 
 * result.add(weather);
 * }
 * 
 * } catch (Exception e) {
 * System.out.println("Weather not found for city " + city);
 * }
 * }
 * 
 * this.repository.saveAll(result);
 * return result;
 * }
 * 
 * }
 */
