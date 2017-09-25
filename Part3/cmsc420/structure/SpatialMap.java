package cmsc420.structure;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.MinimumSpanningTree;
import cmsc420.exceptions.*;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.structure.PMQuadtree.PMQuadtree;
import cmsc420.structure.PRQuadtree.PRQuadtree;
import cmsc420.structure.comparators.AsciiComparator;
import cmsc420.structure.comparators.CityNameComparator;
import cmsc420.structure.comparators.CoordinateComparator;
import cmsc420.structure.comparators.PointCoordinateComparator;

public class SpatialMap {
	AvlGTree<String, City> cities;
	TreeMap<String, City> citiesBackup;
	AvlGTree<String, Airport> airports;
	AvlGTree<String, Terminal> terminals;
	AvlGTree<Point2D, Metropole> metropoles;
	TreeSet<City> cityCoordinates;
	TreeSet<Airport> airportCoordinates;
	TreeSet<Terminal> terminalCoordinates;
	PRQuadtree map;

	//Because creating exceptions is expensive, make them all at the start
	private static MyException duplicateCityCoordinatesException = new MyException("duplicateCityCoordinates");
	private static MyException duplicateAirportCoordinatesException = new MyException("duplicateAirportCoordinates");
	private static MyException duplicateTerminalCoordinatesException = new MyException("duplicateTerminalCoordinates");

	private static MyException duplicateCityNameException = new MyException("duplicateCityName");
	private static MyException duplicateAirportNameException = new MyException("duplicateAirportName");
	private static MyException duplicateTerminalNameException = new MyException("duplicateTerminalName");

	private static MyException cityDoesNotExistException = new MyException("cityDoesNotExist");
	private static MyException airportDoesNotExistException = new MyException("airportDoesNotExist");
	private static MyException terminalDoesNotExistException = new MyException("terminalDoesNotExist");
	private static MyException connectingCityDoesNotExistException = new MyException("connectingCityDoesNotExist");
	private static MyException startPointDoesNotExistException = new MyException("startPointDoesNotExist");
	private static MyException endPointDoesNotExistException = new MyException("endPointDoesNotExist");

	private static MyException noCitiesToListException = new MyException("noCitiesToList");

	private static MyException connectingCityNotInSameMetropoleException = new MyException("connectingCityNotInSameMetropole");
	private static MyException airportNotInSameMetropoleException = new MyException("airportNotInSameMetropole");

	private static MyException roadNotInOneMetropoleException = new MyException("roadNotInOneMetropole");

	private static MyException airportOutOfBoundsException = new MyException("airportOutOfBounds");
	private static MyException terminalOutOfBoundsException = new MyException("terminalOutOfBounds");
	private static MyException metropoleOutOfBoundsException = new MyException("metropoleOutOfBounds");

	private static MyException startEqualsEndException = new MyException("startEqualsEnd");

	private static MyException roadNotMappedException = new MyException("roadNotMapped");
	private static MyException cityNotMappedException = new MyException("cityNotMapped");
	private static MyException noCitiesExistInRangeException = new MyException("noCitiesExistInRange");
	private static MyException cityNotFoundException = new MyException("cityNotFound");

	private static MyException metropoleIsEmptyException = new MyException("metropoleIsEmpty");
	private static MyException emptyTreeException = new MyException("emptyTree");

	int localHeight;
	int localWidth;
	int order;
	int g;

	public SpatialMap(int spatialWidth, int spatialHeight, int localWidth, int localHeight, int pmOrder, int g) {
		cities = new AvlGTree<String, City>(AsciiComparator.getInstance(), g);
		airports = new AvlGTree<String, Airport>(AsciiComparator.getInstance(), g);
		citiesBackup = new TreeMap<String, City>(AsciiComparator.getInstance());
		terminals = new AvlGTree<String, Terminal>(AsciiComparator.getInstance(), g);

		cityCoordinates = new TreeSet<City>(CoordinateComparator.getInstance());
		airportCoordinates = new TreeSet<Airport>(CoordinateComparator.getInstance());
		terminalCoordinates = new TreeSet<Terminal>(CoordinateComparator.getInstance());
		map = new PRQuadtree(spatialWidth, spatialHeight);

		metropoles = new AvlGTree<Point2D, Metropole>(PointCoordinateComparator.getInstance(), g);
		this.localHeight = localHeight;
		this.localWidth = localWidth;
		this.order = pmOrder;
		this.g = g;
	}

	public int getLocalHeight() {
		return localHeight;
	}

	public int getLocalWidth() {
		return localWidth;
	}

	/**
	 * Creates a city and adds it to the list but does not map it.
	 * @param name Name of the city
	 * @param localX X coordinate of the city in the local map
	 * @param localY Y coordinate of the city in the local map
	 * @param remoteX X coordinate of the local map
	 * @param remoteY Y coordinate of the local map
	 * @param radius Radius of the city
	 * @param color Color of the City
	 * @throws MyException
	 */
	public void createCity(String name, int localX, int localY, int remoteX, int remoteY, int radius, String color) throws MyException {
		City c = new City(name, localX, localY, remoteX, remoteY, radius, color);

		//Check if the coordinates is the same as any other in the lists
		if (cityCoordinates.contains(c) || terminalCoordinates.contains(c) || airportCoordinates.contains(c))
			throw duplicateCityCoordinatesException;
		//Check if the name is the same as any other in the lists
		if (cities.containsKey(name) || airports.containsKey(name) || terminals.containsKey(name))
			throw duplicateCityNameException;

		citiesBackup.put(name, c);
		cities.put(name, c);
		cityCoordinates.add(c);
	}

	/**
	 * Clears all of the lists in this structure and the map
	 */
	public void clear() {
		cities.clear();
		citiesBackup.clear();
		airports.clear();
		terminals.clear();
		metropoles.clear();
		cityCoordinates.clear();
		airportCoordinates.clear();
		terminalCoordinates.clear();
		map.clear();
	}

	/**
	 * Deletes the city and unmaps it if mapped. If the city is unmapped, 
	 * remove its connecting roads from the map too.
	 * @param name The name of the city to delete
	 * @return Collection
	 * @throws MyException
	 */
	public ArrayList<Geometry2D> deleteCity(String name) throws MyException {
		//Check if the city exists
		if (!cities.containsKey(name))
			throw cityDoesNotExistException;
		City city = cities.remove(name);
		cityCoordinates.remove(city);
		Point2D metropolePoint = new Point2D.Double(city.getRemoteX(), city.getRemoteY());

		Metropole metro = metropoles.get(metropolePoint);
		if (metro != null)
			return metro.unmapCity(city);

		return new ArrayList<Geometry2D>();
	}

	/**
	 * Gets a list of cities in an order based on the sortBy parameter.
	 * @param sortBy The order to sort the list by.
	 * @return A collection of Cities
	 * @throws MyException
	 */
	public Collection<City> listCities(String sortBy) throws MyException {
		if (cities.isEmpty() || cityCoordinates.isEmpty())
			throw noCitiesToListException;

		if (sortBy.equals("name"))
			return cities.values();
		else
			return cityCoordinates;
	}

	/**
	 * Adds an airport to the map. Also maps a terminal to the map and
	 * connects a city to the terminal by road.
	 * @param name Name of the airport
	 * @param localX X coordinate of the airport
	 * @param localY Y coordinate of the airport
	 * @param remoteX X coordinate of the metropolis
	 * @param remoteY Y coordinate of the metropolis
	 * @param terminalName Name of the terminal
	 * @param terminalX X coordinate of the terminal
	 * @param terminalY Y coordinate of the terminal
	 * @param terminalCity Name of the connecting city
	 * @throws MyException
	 */
	public void mapAirport(String name, int localX, int localY, int remoteX, int remoteY, String terminalName, int terminalX, int terminalY, String terminalCity) throws MyException {
		Airport airport = new Airport(name, localX, localY, remoteX, remoteY);
		if (airports.containsKey(name) || cities.containsKey(name) || terminals.containsKey(name))
			throw duplicateAirportNameException;
		if (cityCoordinates.contains(airport) || airportCoordinates.contains(airport) || terminalCoordinates.contains(airport))
			throw duplicateAirportCoordinatesException;

		Point2D metropolePoint = new Point2D.Double(remoteX, remoteY);

		if (!map.isInBounds(metropolePoint))
			throw airportOutOfBoundsException;
		if (!metropoles.containsKey(metropolePoint))
			metropoles.put(metropolePoint, new Metropole(localWidth, localHeight, order, g));

		Metropole metropolis = metropoles.get(metropolePoint);
		if (!metropolis.isInBounds(airport))
			throw airportOutOfBoundsException;

		Terminal terminal = new Terminal(terminalName, terminalX, terminalY, remoteX, remoteY);
		if (terminals.containsKey(terminalName) || cities.containsKey(terminalName) || airports.containsKey(terminalName))
			throw duplicateTerminalNameException;
		if (terminalCoordinates.contains(terminal) || cityCoordinates.contains(terminal) || airportCoordinates.contains(terminal))
			throw duplicateTerminalCoordinatesException;
		if (!metropolis.isInBounds(terminal))
			throw terminalOutOfBoundsException;
		if (!cities.containsKey(terminalCity))
			throw connectingCityDoesNotExistException;
		City city = cities.get(terminalCity);
		if (city.getRemoteX() != remoteX || city.getRemoteY() != remoteY)
			throw connectingCityNotInSameMetropoleException;

		terminal.setTerminalCity(city);
		terminal.setTerminalAirport(airport);

		metropolis.mapAirport(airport, terminal);

		airports.put(name, airport);
		airportCoordinates.add(airport);
		terminals.put(terminalName, terminal);
		terminalCoordinates.add(terminal);
	}

	/**
	 * Adds a terminal to the map and connects it to the city by road.
	 * Also connects itself to the airport, just not by road.
	 * @param name Name of the terminal
	 * @param localX X coordinate of the terminal
	 * @param localY Y coordinate of the terminal
	 * @param remoteX X coordinate of the metropolis
	 * @param remoteY Y coordinate of the metropolis
	 * @param airportName Name of the connecting airport
	 * @param cityName Name of the connecting city
	 * @throws MyException
	 */
	public void mapTerminal(String name, int localX, int localY, int remoteX, int remoteY, String airportName, String cityName) throws MyException {
		Terminal terminal = new Terminal(name, localX, localY, remoteX, remoteY);
		if (cities.containsKey(name) || airports.containsKey(name) || terminals.containsKey(name))
			throw duplicateTerminalNameException;
		if (cityCoordinates.contains(terminal) || airportCoordinates.contains(terminal) || terminalCoordinates.contains(terminal))
			throw duplicateTerminalCoordinatesException;

		Point2D metropolePoint = new Point2D.Double(remoteX, remoteY);

		if (!map.isInBounds(metropolePoint))
			throw terminalOutOfBoundsException;
		if (!metropoles.containsKey(metropolePoint))
			metropoles.put(metropolePoint, new Metropole(localWidth, localHeight, order, g));

		Metropole metropolis = metropoles.get(metropolePoint);
		if (!metropolis.isInBounds(terminal))
			throw terminalOutOfBoundsException;
		if (!airports.containsKey(airportName))
			throw airportDoesNotExistException;

		Airport airport = airports.get(airportName);
		if (airport.getRemoteX() != remoteX || airport.getRemoteY() != remoteY)
			throw airportNotInSameMetropoleException;
		if (!cities.containsKey(cityName))
			throw connectingCityDoesNotExistException;

		City city = cities.get(cityName);
		if (city.getRemoteX() != remoteX || city.getRemoteY() != remoteY)
			throw connectingCityNotInSameMetropoleException;

		terminal.setTerminalAirport(airport);
		terminal.setTerminalCity(city);

		metropolis.mapTerminal(terminal);

		terminals.put(name, terminal);
		terminalCoordinates.add(terminal);
	}

	/**
	 * @throws InvalidRegionSizeException 
	 * @throws EdgeIntersectsAnotherEdgeException 
	 * @throws PMRuleViolationException 
	 * Adds a road to the map.
	 * NOTE: Road is bidirectional so order of start and end do not matter.
	 * NOTE: This method only connects two cities; not a city and a terminal
	 * @param start The start city 
	 * @param end The end city
	 * @throws MyException
	 * @throws  
	 */
	public void mapRoad(String start, String end) throws MyException {
		City startCity = null, endCity = null;
		Point2D metropolePoint = null;
		Metropole metropolis = null;
		Road road = null;

		//		 try {
		if (!cities.containsKey(start))
			throw startPointDoesNotExistException;
		if (!cities.containsKey(end))
			throw endPointDoesNotExistException;
		if (start.equals(end))
			throw startEqualsEndException;

		startCity = cities.get(start);
		endCity = cities.get(end);

		if (startCity.getRemoteX() != endCity.getRemoteX() || startCity.getRemoteY() != endCity.getRemoteY())
			throw roadNotInOneMetropoleException;

		metropolePoint = new Point2D.Double(startCity.getRemoteX(), startCity.getRemoteY());

		if (!metropoles.containsKey(metropolePoint))
			metropoles.put(metropolePoint, new Metropole(localWidth, localHeight, order, g));

		metropolis = metropoles.get(metropolePoint);
		road = new Road(startCity, endCity);
		metropolis.mapRoad(road);
	}

	/**
	 * Removes a road from the map.
	 * NOTE: Road is bidirectional so order of start and end do not matter.
	 * NOTE: This method only disconnects two cities; not a city and a terminal
	 * @param start The start city
	 * @param end The end city
	 * @throws MyException
	 */
	public void unmapRoad(String start, String end) throws MyException {
		if (!cities.containsKey(start))
			throw startPointDoesNotExistException;
		if (!cities.containsKey(end))
			throw endPointDoesNotExistException;
		if (start.equals(end))
			throw startEqualsEndException;

		City startCity = cities.get(start);
		City endCity = cities.get(end);

		Point2D metroPoint = new Point2D.Double(startCity.getRemoteX(), startCity.getRemoteY());
		Metropole metropolis = metropoles.get(metroPoint);
		Road road = startCity.getConnectingRoad(endCity);

		if (metropolis == null)
			throw roadNotMappedException;
		metropolis.unmapRoad(road);

		if (metropolis.isEmpty())
			metropoles.remove(metroPoint);
	}

	/**
	 * Removes an airport from the map. Also removes
	 * all of its connecting terminals.
	 * @param name Name of the airport to remove.
	 * @throws MyException
	 * @throws PMRuleViolationException 
	 */
	public ArrayList<Terminal> unmapAirport(String name) throws MyException {
		if (!airports.containsKey(name))
			throw airportDoesNotExistException;

		Airport airport = airports.remove(name);
		airportCoordinates.remove(airport);

		Point2D metroPoint = new Point2D.Double(airport.getRemoteX(), airport.getRemoteY());
		Metropole metropolis = metropoles.get(metroPoint);

		if (metropolis == null)
			throw airportDoesNotExistException;
		ArrayList<Terminal> removedTerminals =  metropolis.unmapAirport(airport);

		if (metropolis.isEmpty())
			metropoles.remove(metroPoint);

		return removedTerminals;
	}

	/**
	 * Removes a terminal from the map. Also removes the road
	 * connecting it to its connecting city. If the airport that
	 * it connects to has no other terminal, that too gets unmapped.
	 * @param name Name of the terminal to remove.
	 * @throws MyException
	 * @throws PMRuleViolationException 
	 */
	public Airport unmapTerminal(String name) throws MyException {
		if (!terminals.containsKey(name))
			throw terminalDoesNotExistException;

		Terminal terminal = terminals.remove(name);
		terminalCoordinates.remove(terminal);

		Point2D metroPoint = new Point2D.Double(terminal.getRemoteX(), terminal.getRemoteY());
		Metropole metropolis = metropoles.get(metroPoint);
		if (metropolis == null)
			throw terminalDoesNotExistException;
		Airport removedAirport = metropolis.unmapTerminal(terminal);

		if (metropolis.isEmpty())
			metropoles.remove(metroPoint);

		return removedAirport;
	}

	/**
	 * Returns the metropolis at remoteX,remoteY
	 * @param remoteX X coordinate of the metropolis
	 * @param remoteY Y coordinate of the metropolis
	 * @return PMQuadtree
	 * @throws MyException
	 */
	public PMQuadtree getMetropole(int remoteX, int remoteY) throws MyException {
		Point2D remotePoint = new Point2D.Double(remoteX, remoteY);

		if (!map.isInBounds(remotePoint))
			throw metropoleOutOfBoundsException;
		if (!metropoles.containsKey(remotePoint))
			throw metropoleIsEmptyException;
		PMQuadtree metropolis = metropoles.get(remotePoint).getMetropolis();
		if (metropolis.isEmpty())
			throw metropoleIsEmptyException;
		return metropolis;
	}

	/**
	 * Returns a collection of all cities within the radius in asciibetical order.
	 * @param remoteX X coordinate of the center of the range
	 * @param remoteY Y coordinate of the center of the range
	 * @param radius Radius of the range
	 * @return PriorityQueue
	 * @throws MyException
	 */
	public ArrayList<City> globalRangeCities(int remoteX, int remoteY, int radius) throws MyException {
		ArrayList<City> citiesInRange = new ArrayList<City>();
		Circle2D range = new Circle2D.Double(remoteX, remoteY, radius);
		for (Point2D point : metropoles.keySet()) {
			if (Inclusive2DIntersectionVerifier.intersects(point, range)) {
				if (metropoles.containsKey(point))
					citiesInRange.addAll(metropoles.get(point).getCities());
			}
		}

		if (citiesInRange.isEmpty())
			throw noCitiesExistInRangeException;

		citiesInRange.sort(CityNameComparator.getInstance());
		return citiesInRange;
	}

	/**
	 * Returns the nearest city from the point localX,localY in the metropolis of remoteX,remoteY
	 * @param localX X coordinate of the point
	 * @param localY Y coordinate of the point
	 * @param remoteX X coordinate of the metropolis
	 * @param remoteY Y coordinate of the metropolis
	 * @return City
	 * @throws MyException
	 */
	public City nearestCity(int localX, int localY, int remoteX, int remoteY) throws MyException {
		Metropole metropolis = metropoles.get(new Point2D.Double(remoteX, remoteY));
		if (metropolis == null)
			throw cityNotFoundException;

		City nearest = metropolis.nearestCity(localX, localY);

		if (nearest == null)
			throw cityNotFoundException;

		return nearest;
	}

	/**
	 * Returns the list of all cities created.
	 * @return AvlGTree
	 * @throws MyException
	 */
	public AvlGTree<String, City> getCities() throws MyException {
		if (cities.isEmpty())
			throw emptyTreeException;

		return cities;
	}

	public MinimumSpanningTree minimumSpanningTree(String start) throws MyException {
		if (!cities.containsKey(start))
			throw cityDoesNotExistException;

		City startCity = cities.get(start);
		if (!metropoles.get(new Point2D.Double(startCity.getRemoteX(), startCity.getRemoteY())).isCityMapped(start))
			throw cityNotMappedException;

		return new MinimumSpanningTree(startCity, metropoles);
	}
}
