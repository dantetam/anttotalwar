package info.comita.entity;

import java.util.ArrayList;

public class Army {

	public ArrayList<Unit> units;
	public int number;
	//public double cX, cY, cZ; //center of entire army
	
	public Army()
	{
		units = new ArrayList<Unit>();
		//units.add(new Unit(0));
		//units.add(new Unit(1));
	}
	
}
