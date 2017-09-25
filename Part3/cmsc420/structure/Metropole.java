package cmsc420.structure;

import java.util.ArrayList;
import java.util.Collection;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.MyException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Geometry2D;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.structure.PMQuadtree.PM1Quadtree;
import cmsc420.structure.PMQuadtree.PM3Quadtree;
import cmsc420.structure.PMQuadtree.PMQuadtree;
import cmsc420.structure.comparators.AsciiComparator;
import cmsc420.structure.comparators.CityDistanceComparator;
import cmsc420.structure.comparators.GeometryNameComparator;

public class Metropole {

	private PMQuadtree metropolis;
	private AvlGTree<String, City> mappedCities;
	private AvlGTree<String, Airport> mappedAirports;
	private AvlGTree<String, Terminal> mappedTerminals;

	private static MyException terminalViolatesPMRulesException = new MyException("terminalViolatesPMRules");
	private static MyException roadIntersectsAnotherRoadException = new MyException("roadIntersectsAnotherRoad");
	private static MyException airportOutOfBoundsException = new MyException("airportOutOfBounds");
	private static MyException airportViolatesPMRulesException = new MyException("airportViolatesPMRules");
	private static MyException connectingCityNotMappedException = new MyException("connectingCityNotMapped");
	private static MyException roadAlreadyMappedException = new MyException("roadAlreadyMapped");

	private static MyException roadOutOfBoundsException = new MyException("roadOutOfBounds");
	private static MyException roadViolatesPMRulesException = new MyException("roadViolatesPMRules");
	private static MyException roadNotMappedException = new MyException("roadNotMapped");


	public Metropole(int maxX, int maxY, int order, int g) {
		mappedCities = new AvlGTree<String, City>(AsciiComparator.getInstance(), g);
		mappedAirports = new AvlGTree<String, Airport>(AsciiComparator.getInstance(), g);
		mappedTerminals = new AvlGTree<String, Terminal>(AsciiComparator.getInstance(), g);
		if (order == 1)
			metropolis = new PM1Quadtree(maxX, maxY);
		else
			metropolis = new PM3Quadtree(maxX, maxY);
	}

	public boolean isCityMapped(String city) {
		return mappedCities.containsKey(city);
	}

	public boolean isAirportMapped(String airport) {
		return mappedAirports.containsKey(airport);
	}

	public boolean isTerminalMapped(String terminal) {
		return mappedTerminals.containsKey(terminal);
	}

	public boolean isRoadMapped(Road road) {
		return metropolis.find(road);
	}

	public void mapCity(City c) throws PMRuleViolationException, InvalidRegionSizeException {
		try {
			metropolis.insert(c);
			mappedCities.put(c.name, c);
		} catch (EdgeIntersectsAnotherEdgeException e) {}

	}

	public ArrayList<Geometry2D> unmapCity(City c) {
		ArrayList<Geometry2D> removedGeometry = new ArrayList<Geometry2D>();

		metropolis.remove(c);
		if (mappedCities.containsKey(c.getName())) {
			mappedCities.remove(c.getName());
			removedGeometry.add(c);
		}

		for (SpatialMapPoint end : c.getConnectingRoads().keySet()) {
			removedGeometry.add(c.getConnectingRoad(end));
			metropolis.remove(c.getConnectingRoad(end));

			if (end.getStucture() == SpatialMapPoint.CITY) {
				((City) end).removeConnectingRoad(end);
				if (((City) end).getConnectingRoads().isEmpty()) {
					mappedCities.remove(end.getName());
					metropolis.remove(end);
				}
			} else
				unmapTerminal((Terminal) end);
		}

		removedGeometry.sort(GeometryNameComparator.getInstance());
		return removedGeometry;
	}

	public void mapAirport(Airport airport, Terminal terminal) throws MyException {
		if (!isInBounds(airport)) {
			throw airportOutOfBoundsException;
		}

		try {
			metropolis.insert(airport);
		} catch (PMRuleViolationException | InvalidRegionSizeException e) {
			throw airportViolatesPMRulesException;
		} catch (EdgeIntersectsAnotherEdgeException e) {}

		try {
			mapTerminal(terminal);
		} catch (MyException e) {
			metropolis.remove(airport);
			throw e;
		}

		mappedAirports.put(airport.name, airport);
	}

	public ArrayList<Terminal> unmapAirport(Airport airport) {
		ArrayList<Terminal> removedTerminals = new ArrayList<Terminal>();
		mappedAirports.remove(airport.getName());
		metropolis.remove(airport);

		for (Terminal terminal : airport.getTerminals()) {
			unmapTerminalFromAirport(terminal);
			removedTerminals.add(terminal);

			//Remove road to connecting city
			Road road = terminal.getTerminalCity().getConnectingRoad(terminal);
			try {
				unmapRoad(road);
			} catch (MyException e) {}
		}

		removedTerminals.sort(GeometryNameComparator.getInstance());
		return removedTerminals;
	}

	public void mapTerminal(Terminal terminal) throws MyException  {
		if (!mappedCities.containsKey(terminal.getTerminalCity().getName()))
			throw connectingCityNotMappedException;

		try {
			metropolis.insert(terminal);
		} catch (PMRuleViolationException e) {
			throw terminalViolatesPMRulesException;
		} catch (InvalidRegionSizeException e) {
			throw terminalViolatesPMRulesException;
		} catch (EdgeIntersectsAnotherEdgeException e) {}

		Road road = new Road(terminal, terminal.getTerminalCity());
		try {
			metropolis.insert(road);
		} catch (InvalidRegionSizeException e) {
			metropolis.remove(terminal);
			throw terminalViolatesPMRulesException;
		} catch (EdgeIntersectsAnotherEdgeException e) {
			metropolis.remove(terminal);
			throw roadIntersectsAnotherRoadException;
		} catch (PMRuleViolationException e) {
			metropolis.remove(terminal);
			throw roadIntersectsAnotherRoadException;
		}

		terminal.getTerminalCity().addConnectingRoad(road);
		Airport airport = terminal.getAirport();
		airport.addTerminal(terminal);
		mappedTerminals.put(terminal.getName(), terminal);
	}

	public void unmapTerminalFromAirport(Terminal terminal) {
		metropolis.remove(terminal);
		mappedTerminals.remove(terminal.getName());
		
		Road road = terminal.getTerminalCity().getConnectingRoad(terminal);
		try {
			unmapRoad(road);
		} catch (MyException e) {}
	}
	
	public Airport unmapTerminal(Terminal terminal) {
		metropolis.remove(terminal);
		mappedTerminals.remove(terminal.getName());

		Airport airport = terminal.getAirport();
		airport.removeTerminal(terminal);
		if (airport.getTerminals().size() == 0)
			unmapAirport(airport);
		else
			airport = null;

		Road road = terminal.getTerminalCity().getConnectingRoad(terminal);
		try {
			unmapRoad(road);
		} catch (MyException e) {}
		return airport;
	}

	public boolean isInBounds(Geometry2D geom) {
		return metropolis.isInBounds(geom);
	}

	public void mapRoad(Road road) throws MyException {
		City start = (City) road.getSMP1();
		City end = (City) road.getSMP2();


		try {
			if (!mappedCities.containsKey(start.getName()) && isInBounds(start))
				mapCity(start);
			if (!mappedCities.containsKey(end.getName()) && isInBounds(end))
				mapCity(end);

			if (!isInBounds(road))
				throw roadOutOfBoundsException;

			if (metropolis.find(road))
				throw roadAlreadyMappedException;

			metropolis.insert(road);
		} catch (EdgeIntersectsAnotherEdgeException | PMRuleViolationException e) {
			if (isCityMapped(start.getName()) && start.getConnectingRoads().isEmpty())
				unmapCity(start);
			if (isCityMapped(end.getName()) && end.getConnectingRoads().isEmpty())
				unmapCity(end);
			throw roadIntersectsAnotherRoadException;
		} catch (InvalidRegionSizeException e) {
			if (isCityMapped(start.getName()) && start.getConnectingRoads().isEmpty())
				unmapCity(start);
			if (isCityMapped(end.getName()) && end.getConnectingRoads().isEmpty())
				unmapCity(end);
			throw roadViolatesPMRulesException;
		}


		start.addConnectingRoad(road);
		end.addConnectingRoad(road);
	}

	public void unmapRoad(Road road) throws MyException {
		if (road == null || !metropolis.find(road))
			throw roadNotMappedException;

		metropolis.remove(road);

		if (road.getSMP1().getStucture() == SpatialMapPoint.CITY) {
			((City) road.getSMP1()).removeConnectingRoad(road.getSMP2());
			if (((City) road.getSMP1()).getConnectingRoads().isEmpty())
				unmapCity((City) road.getSMP1());
		}
		if (road.getSMP2().getStucture() == SpatialMapPoint.CITY) {
			((City) road.getSMP2()).removeConnectingRoad(road.getSMP1());
			if (((City) road.getSMP2()).getConnectingRoads().isEmpty())
				unmapCity((City) road.getSMP2());
		}
	}

	public PMQuadtree getMetropolis() {
		return metropolis;
	}

	public Collection<City> getCities() {
		return mappedCities.values();
	}

	public boolean isEmpty() {
		return mappedCities.isEmpty() &&
				mappedAirports.isEmpty() &&
				mappedTerminals.isEmpty();
	}

	public City nearestCity(int localX, int localY) {
		Geometry2D nearest = metropolis.nearestElement(new CityDistanceComparator(localX, localY));

		if (nearest.getType() == Geometry2D.POINT)
			return (City) nearest;
		else
			return null;
	}

	public Collection<Airport> getAirports() {
		return mappedAirports.values();
	}

	public Collection<Terminal> getTerminals() {
		return mappedTerminals.values();
	}
}
