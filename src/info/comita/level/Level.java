package info.comita.level;

import java.util.ArrayList;

import info.comita.entity.*;
import info.comita.math.PerlinNoise;
import info.comita.render.Entity;

public class Level {

	public ArrayList<Army> armies;
	public PerlinNoise perlinNoise;
	public ArrayList<Entity> terrain; 

	public Level()
	{
		armies = new ArrayList<Army>();
		terrain = new ArrayList<Entity>();
		setup();
	}

	public void setup()
	{
		Army army1 = new Army();
		//Army army2 = new Army();
		armies.add(army1);
		army1.number = 0;
		new ArmyConstructor(army1,new int[]{4,2,3,5,1},0,0,0,0);

		Army army2 = new Army();
		//Army army2 = new Army();
		armies.add(army2);
		army2.number = 1;
		new ArmyConstructor(army2,new int[]{2,2,2},0,0,-70,1);

		double[][] oldSource = new PerlinNoise(870691).makePerlinNoise(16,16,3,8,3,0.5,2);
		double[][] source = PerlinNoise.recurInter(oldSource,4,2);
		float len = 50;
		for (int r = 0; r < source.length; r++)
		{
			for (int c = 0; c < source[0].length; c++)
			{
				Entity en = new Entity();
				en.sX = len;
				en.sY = (float) (len*source[r][c]) - 200;
				en.sZ = len;
				en.rotX = 0; en.rotY = 0; en.rotZ = 0;
				en.r = (float)Math.random()*50 + 30; en.g = (float)Math.random()*200 + 50; en.b = 0;
				en.x = r*len - source.length*len/2;
				en.y = en.sY/2;
				en.z = c*len - source.length*len/2;
				terrain.add(en);
			}
		}
	}

}
