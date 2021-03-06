package plm.universe.turtles;

import java.awt.Color;
import java.awt.Graphics2D;

public class Circle implements Shape {
	double x,y,radius;
	Color color;
	public Circle(double x,double y, double radius, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}
	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.drawOval((int) (x-radius), (int) (y-radius), (int) (2.*radius), (int) (2.*radius));
	}
	@Override
	public Shape copy() {
		return new Circle(x, y, radius, color);
	}
	private boolean doubleEqual(double a, double b) {
		return (Math.abs(a-b)<0.000001);
	}
	@Override
	public String diffTo(Shape o) {
		if (o instanceof Circle) {
			Circle other = (Circle) o;
			if (!doubleEqual(x,other.x))
				return "x differs";
			if (!doubleEqual(y,other.y))
				return "y differs";
			if (!doubleEqual(radius,other.radius))
				return "radius differs";
			return "I dont see the difference";
		} else 
			return "That's not a line";
	}
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Circle))
			return false;
		
		Circle other = (Circle) obj;
		if (!doubleEqual(x,other.x))
			return false;
		if (!doubleEqual(y,other.y))
			return false;
		if (!doubleEqual(radius,other.radius))
			return false;
				
		return true;
	}

}
