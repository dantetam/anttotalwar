package info.comita.system;

import info.comita.entity.Ant;
import info.comita.entity.Unit;

import java.util.ArrayList;

public class Command {

	public String type; //a command, like move
	public ArrayList<Double> data; //some background e.g. where to move
	public ArrayList<double[]> otherData;
	public int duration; //in frames
	public Unit obj;
	public Ant obj2;
	public boolean removeMe = false;
	public boolean canCancel = true;
	private Command chain = null;
	
	public Command(String type, double[] numbers, int duration, Unit obj, Ant obj2)
	{
		data = new ArrayList<Double>();
		otherData = new ArrayList<double[]>();
		for (int i = 0; i < numbers.length; i++)
		{
			data.add(numbers[i]);
		}
		this.type = type;
		this.duration = duration;
		this.obj = obj;
		this.obj2 = obj2;
		if (type.equals("rout") || type.equals("delayedkill"))
		{
			canCancel = false;
		}
	}
	
	//Set a command that immediately, i.e. when you're done shooting the arrow, show the blood
	public void chain(Command c)
	{
		chain = c;
	}
	
	public Command getChain()
	{
		return chain;
	}
	
}
