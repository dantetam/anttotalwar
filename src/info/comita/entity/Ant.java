package info.comita.entity;

public class Ant {

	public double oX, oY, oZ; //location
	public int sX, sY, sZ; //size
	public double rot; //rotation on y axis in angles
	public boolean dead = false;
	public double dX, dY, dZ; //pos of death
	public double theta = 0;
	
	public Ant()
	{
		sX = 1;
		sY = 1;
		sZ = 1;
		rot = 0;
	}
	
	public void die(Unit u)
	{
		dead = true;
		dX = u.cX + oX;
		dY = u.cY + oY;
		dZ = u.cZ + oZ;
	}
	
}
