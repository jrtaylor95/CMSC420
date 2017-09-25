package cmsc420.meeshquest.part1;
import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class City extends Point2D.Float{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String color;
	private float radius;
	
	public City(String name, String color) {
		super.x = 0;
		super.y = 0;
		this.radius = 0;
		this.name = name;
		this.color = color;
	}
	
	public City(String name, float x, float y, float radius, String color) {
		super.x = x;
		super.y = y;
		this.name = name;
		this.radius = radius;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public boolean equals(City c) {
		return this.y == c.y && this.x == c.x && this.name.equals(c.name);
	}
	
	public Element printToXml(Document results) {
		Element city = results.createElement("city");
		city.setAttribute("name", name);
		city.setAttribute("x", Integer.toString((int) x));
		city.setAttribute("y", Integer.toString((int) y));
		city.setAttribute("radius", Integer.toString((int) radius));
		city.setAttribute("color", color);
		
		return city;
	}
}
