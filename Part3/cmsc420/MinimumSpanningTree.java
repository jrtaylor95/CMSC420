package cmsc420;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.geom.Geometry2D;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.structure.Airport;
import cmsc420.structure.City;
import cmsc420.structure.Metropole;
import cmsc420.structure.SpatialMapPoint;
import cmsc420.structure.Terminal;
import cmsc420.structure.comparators.AsciiComparator;

public class MinimumSpanningTree {

	TreeMap<String, Double> distances;
	TreeMap<String, String> prev;
	TreeMap<String, TreeSet<String>> next;
	PriorityQueue<GeometryDistanceCouple> queue;
	ArrayList<String> marked;
	String lastNode;

	public MinimumSpanningTree(City startPoint, AvlGTree<Point2D, Metropole> map) {
		distances = new TreeMap<String, Double>(AsciiComparator.getInstance());
		prev = new TreeMap<String, String>(AsciiComparator.getInstance());
		marked = new ArrayList<String>();
		queue = new PriorityQueue<GeometryDistanceCouple>();
		next = new TreeMap<String, TreeSet<String>>(AsciiComparator.getInstance());

		for (Metropole metro : map.values()) {
			for (City city : metro.getCities()) {
				if (!city.equals(startPoint))
					distances.put(city.getName(), Double.POSITIVE_INFINITY);
				else
					distances.put(city.getName(), 0.0);
			}

			for (Terminal terminal : metro.getTerminals()) {
				distances.put(terminal.getName(), Double.POSITIVE_INFINITY);
			}

			for (Airport airport : metro.getAirports()) {
				distances.put(airport.getName(), Double.POSITIVE_INFINITY);
			}
		}

		prim(startPoint, map);
	}

	private void prim(City city, AvlGTree<Point2D, Metropole> map) {
		queue.add(new GeometryDistanceCouple(city, 0.0));

		while(!queue.isEmpty()) {
			lastNode = queue.peek().geometry.getName();
			scan(queue.remove().geometry, map);
		}
	}

	private void scan(SpatialMapPoint point, AvlGTree<Point2D, Metropole> map) {
		marked.add(point.getName());
		if (point.getStucture() == SpatialMapPoint.CITY)
			scan((City) point);
		else if (point.getStucture() == SpatialMapPoint.TERMINAL)
			scan((Terminal) point);
		else
			scan((Airport) point, map);
	}

	private void scan(City city) {
		for (SpatialMapPoint end : city.getConnectingRoads().keySet()) {

			check(city, end, city.getConnectingRoad(end).getLength());
		}
	}

	private void scan(Terminal terminal) {
		Airport airport = terminal.getAirport();
		City city = terminal.getTerminalCity();

		check(terminal, city, city.distance(terminal));

		check(terminal, airport, airport.distance(terminal));
	}

	private void scan(Airport airport, AvlGTree<Point2D, Metropole> map) {
		for (Point2D point : map.keySet()) {
			for (Airport other : map.get(point).getAirports()) {
				double distance = (new Point2D.Double(airport.getRemoteX(), airport.getRemoteY()).distance(other.getRemoteX(), other.getRemoteY()));
				check(airport, other, distance);
			}
		}
		for (Terminal terminal : airport.getTerminals()) {
			double distance = airport.distance(terminal);
			check(airport, terminal, distance);
		}
	}
	
	private void check(SpatialMapPoint point, SpatialMapPoint end, double distance) {
		if (!marked.contains(end.getName())) {
			if (distance == distances.get(end.getName())) {
				if (point.getName().compareTo(prev.get(end.getName())) > 0)
					update(point, end);
			} else if (distance < distances.get(end.getName()))
				add(point, end, distance);
		}
	}
	
	private void update(SpatialMapPoint point, SpatialMapPoint end) {
		next.get(prev.get(end.getName())).remove(end.getName());
		prev.put(end.getName(), point.getName());
		if (next.containsKey(point.getName()))
			next.get(point.getName()).add(end.getName());
		else {
			TreeSet<String> neighborList = new TreeSet<String>(AsciiComparator.getInstance());
			neighborList.add(end.getName());
			next.put(point.getName(), neighborList);
		}
	}
	private void add(SpatialMapPoint point, SpatialMapPoint end, double distance) {
		distances.put(end.getName(), distance);
		prev.put(end.getName(), point.getName());
		if (next.containsKey(point.getName()))
			next.get(point.getName()).add(end.getName());
		else {
			TreeSet<String> neighborList = new TreeSet<String>(AsciiComparator.getInstance());
			neighborList.add(end.getName());
			next.put(point.getName(), neighborList);
		}
		queue.add(new GeometryDistanceCouple(end, distance));
	}

	public double getDistanceSpanned() {
		double distance = 0;

		for (Double road : distances.values()) {
			distance += road;
		}

		return distance;
	}

	public String getLastNode() {
		return lastNode;
	}

	public TreeMap<String, String> getPrevNodes() {
		return prev;
	}
	
	public TreeMap<String, TreeSet<String>> getNext() {
		return next;
	}
}

class GeometryDistanceCouple implements Comparable<GeometryDistanceCouple>{

	SpatialMapPoint geometry;
	Double distance;

	public GeometryDistanceCouple(SpatialMapPoint g, Double distance) {
		this.geometry = g;
		this.distance = distance;
	}

	@Override
	public int compareTo(GeometryDistanceCouple o) {
		return distance.compareTo(o.distance);
	}

	public void setDistance(Double newDistance) {
		this.distance = newDistance;
	}

	public Geometry2D getGeometry() {
		return geometry;
	}
}
