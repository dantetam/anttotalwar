package info.comita.math;

//A plane in the form Ax + By + Cz = D

public class Plane {

	public double a,b,c;
	public double d;
	
	public Plane(double a, double b, double c, double d)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Point intersect(Line l)
	{
		return l.intersect(this);
	}
	
}
