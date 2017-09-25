package cmsc420.PMQuadtree;

import java.util.TreeMap;
import java.util.TreeSet;

import cmsc420.Structure.GeometryNameComparator;
import cmsc420.geom.Geometry2D;

public class AdjacencyList {
	TreeMap<String, TreeSet<Geometry2D>> list;
	
	public AdjacencyList() {
		list = new TreeMap<String, TreeSet<Geometry2D>>();
	}
	
	public void add(String key, Geometry2D edge) {
		TreeSet<Geometry2D> neighborList = list.get(key);
		
		if (neighborList == null)
			neighborList = new TreeSet<Geometry2D>(new GeometryNameComparator());
		
		neighborList.add(edge);
		
		list.put(key, neighborList);
	}
	
	public TreeSet<Geometry2D> get(String str) {
		return list.get(str);
	}
}
