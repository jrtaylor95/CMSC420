package cmsc420.Structure;

import java.awt.geom.Point2D;

import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;

public class City extends Point2D implements Geometry2D {
	private String name;
	private String color;
	private int radius;
	int x, y;
	boolean isolated;

	public City(String name, String color) {
		x = 0;
		y = 0;
		this.radius = 0;
		this.name = name;
		this.color = color;
		isolated = false;
	}

	public City(String name, int x, int y, int radius, String color) {
		this.x = x;
		this.y = y;
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

	public void toXml(Element parent) {
		Element city;

		city = parent.getOwnerDocument().createElement("city");

		city.setAttribute("name", name);
		city.setAttribute("x", Integer.toString(x));
		city.setAttribute("y", Integer.toString(y));
		city.setAttribute("radius", Integer.toString(radius));
		city.setAttribute("color", color);

		parent.appendChild(city);
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public int getType() {
		return Geometry2D.POINT;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public boolean equals(Geometry2D g2) {
		if (g2.getType() == Geometry2D.POINT)
			return equals((City) g2);

		return false;
	}
}
