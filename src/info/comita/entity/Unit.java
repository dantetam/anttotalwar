package info.comita.entity;

import info.comita.data.Data;
import info.comita.system.Runner;

import java.util.ArrayList;

public class Unit {

	public ArrayList<Ant> occupants;

	public double cX, cY, cZ; //center of unit
	public boolean selected = false; //selected by player
	public double sX, sY;

	public int typeId;
	public static int globalId = 0;
	public int id;
	public int side;
	public int category; //0 light infantry, 1 light cavalry, 2 heavy cavalry

	public int attack, defense;
	public double speed; //speed is per studs per frame
	//public int size; //number of troops
	public String name;
	public double morale; //0 to 10 scale. below two, unit routs, above five, unit returns. zero, unit routes forever
	public double lastCount; //how many ants were alive last second

	public boolean walkingThrough = false;
	public boolean charging = false;
	public boolean fighting = false;
	//public boolean flanked = false;
	public boolean outnumbered = false;
	public boolean retreating = false;
	public boolean routing = false;
	//public boolean running = false;

	public double speedModifier = 1;
	public double attackModifier = 1;
	public double defenseModifier = 1;

	/*public Unit()
	{
		occupants = new ArrayList<Ant>();
		cX = 0;
		cY = 0;
		cZ = 0;
		for (int i = 0; i < 81; i++)
		{
			Ant ant = new Ant();
			ant.oX = Math.random()*50 - 25;
			ant.oY = 0;
			ant.oZ = Math.random()*30 - 15;
			occupants.add(ant);
		}
	}*/

	public Unit(int typeId)
	{
		this.typeId = typeId;
		id = globalId;
		globalId++;

		Unit unit = Data.units.get(typeId);
		occupants = new ArrayList<Ant>();
		cX = 0;
		cY = 0;
		cZ = 0;
		this.speed = unit.speed;
		this.sX = unit.sX;
		this.sY = unit.sY;
		this.attack = unit.attack;
		this.defense = unit.defense;
		this.morale = unit.morale;
		for (int i = 0; i < unit.occupants.size(); i++)
		{
			Ant ant = new Ant();
			ant.oX = Math.random()*unit.sX - unit.sX/2;
			ant.oY = 0;
			ant.oZ = Math.random()*unit.sY - unit.sY/2;
			occupants.add(ant);
		}
		lastCount = unit.occupants.size();
		this.name = unit.name;
		this.category = unit.category;
	}

	public Unit(int typeId, int attack, int defense, int morale, int size, double speed, double sX, double sY, String name, int category)
	{
		this.typeId = typeId;
		id = globalId;
		globalId++;

		occupants = new ArrayList<Ant>();
		cX = 0;
		cY = 0;
		cZ = 0;
		this.speed = speed;
		this.sX = sX;
		this.sY = sY;
		this.attack = attack;
		this.defense = defense;
		this.morale = morale;
		for (int i = 0; i < size; i++)
		{
			Ant ant = new Ant();
			ant.oX = Math.random()*sX - sX/2;
			ant.oY = 0;
			ant.oZ = Math.random()*sY - sY/2;
			occupants.add(ant);
		}
		lastCount = size;
		this.name = name;
		this.category = category;
	}

	public void moveTo(int cX, int cY, int cZ)
	{
		this.cX = cX;
		this.cY = cY;
		this.cZ = cZ;
	}

	public void move(int cX, int cY, int cZ)
	{
		this.cX += cX;
		this.cY += cY;
		this.cZ += cZ;
	}

	public double countDead()
	{
		double dead = 0;
		for (int i = 0; i < occupants.size(); i++)
		{
			if (occupants.get(i).dead)
				dead++;
		}
		if (dead == 0) return 0;
		return dead;
		/*//System.out.println( dead/(dead+(double)occupants.size()) );
		return dead/(dead+(double)occupants.size());*/
	}

	//Find an ant that's sort of close to die. :)
	public Ant findClosestPoint(Unit o)
	{
		//Indices correspond with pos in array
		/*double[] distances = new double[occupants.size()];
		double average = 0;
		double max = 0;
		for (int i = 0; i < occupants.size(); i++)
		{
			double dist = dist(x, z, cX, cZ);
			distances[i] = dist;
			//For calculating average
			if (dist > max) max = dist;
			average += dist;
		}
		average /= occupants.size();
		//ArrayList<Integer> small = new ArrayList<Integer>();
		ArrayList<Integer> cor = new ArrayList<Integer>();
		for (int i = 0; i < distances.length; i++)
		{
			if (distances[i] < average - (max-average)*3/4)
			{
				//small.add((int)distances[i]);
				cor.add(i);
			}
			else
			{

				//Disregard result.
			}
		}
		if (cor.size() == 0) return null;
		return occupants.get(cor.get((int) (Math.random()*cor.size())));*/
		double maxDist = Math.sqrt(Math.pow(o.sX/2,2) + Math.pow(o.sY/2,2));
		for (int i = (int)(Math.random()*occupants.size()); i < occupants.size(); i++)
		{
			if (dist(occupants.get(i).oX + cX, occupants.get(i).oZ + cZ, o.cX, o.cZ) < maxDist)
			{
				if (!occupants.get(i).dead)
					//System.out.println("found");
					return occupants.get(i);
			}
		}
		return null;
	}

	//Redefine size in 2d based on alive ants
	public void reform()
	{
		double minX = occupants.get(0).oX;
		double maxX = occupants.get(0).oX;
		double minZ = occupants.get(0).oZ;
		double maxZ = occupants.get(0).oZ;
		for (int i = 1; i < occupants.size(); i++)
		{
			if (!occupants.get(i).dead)
			{
				if (occupants.get(i).oX < minX) minX = occupants.get(i).oX;
				else if (occupants.get(i).oX > maxX) maxX = occupants.get(i).oX;
				if (occupants.get(i).oZ < minZ) minZ = occupants.get(i).oZ;
				else if (occupants.get(i).oZ > maxZ) maxZ = occupants.get(i).oZ;
			}
		}
		sX = maxX - minX;
		sY = maxZ - minZ;
		//cX = sX/2 + minX;
		//cZ = sY/2 + minZ;
	}

	public double dist(double x1, double z1, double x2, double z2)
	{
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));
	}

	public boolean equals(Unit u)
	{
		return id == u.id;
	}

}
