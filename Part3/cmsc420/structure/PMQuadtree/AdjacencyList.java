package cmsc420.structure.PMQuadtree;

import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.comparators.GeometryNameComparator;


public class AdjacencyList {
	TreeMap<String, TreeSet<Geometry2D>> list;
	
	public AdjacencyList() {
		list = new TreeMap<String, TreeSet<Geometry2D>>();
	}
	
	public void add(String key, Geometry2D edge) {
		TreeSet<Geometry2D> neighborList = list.get(key);
		
		if (neighborList == null)
			neighborList = new TreeSet<Geometry2D>(GeometryNameComparator.getInstance());
		
		neighborList.add(edge);
		
		list.put(key, neighborList);
	}
	
	public TreeSet<Geometry2D> get(String str) {
		return list.get(str);
	}
}
