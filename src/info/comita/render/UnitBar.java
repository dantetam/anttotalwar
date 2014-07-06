package info.comita.render;

import info.comita.entity.Army;
import info.comita.entity.Unit;
import info.comita.system.Runner;
import processing.core.PApplet;

public class UnitBar extends PApplet {

	public Runner runner;
	public int boxSize = 100;

	public UnitBar(Runner runner)
	{
		this.runner = runner;
	}

	public void setup()
	{
		size(1600,100);
		noLoop();
	}

	public void draw()
	{
		background(0);
		Army a = runner.level.armies.get(0);
		for (int i = 0; i < a.units.size(); i++)
		{
			Unit u = a.units.get(i);
			fill(255);
			stroke(0);
			if (u.selected)
				fill(0,0,255);
			else if (u.routing)
				fill(200,0,0);
			else if (u.countDead() == u.occupants.size())
				continue;
			rect(i*boxSize,0,boxSize,boxSize);
			fill(0);
			if (u.selected)
				fill(255);
			text(u.name,(float)i*boxSize,50);
			text((int)(u.occupants.size() - u.countDead()) + "/" + u.occupants.size(),(float)i*boxSize,70);
		}
		if (ctt != null)
		{
			fill(0,0,255);
			rect(1600-boxSize*3,0,boxSize*3,boxSize);
			fill(255);
			text(ctt.name,1650 - boxSize*3,50);
			text((int)(ctt.occupants.size() - ctt.countDead()) + "/" + ctt.occupants.size(),1650 - boxSize*3,70);
			text((int)ctt.morale + "/10",1650 - boxSize*3,90);
		}
		
	}

	public Unit ctt = null; //current tool tip
	public void forwardUnitTip(Unit u)
	{
		ctt = u;
	}
	
	public void mousePressed()
	{
		/*int numSelecting = mouseX/boxSize;
		//Deselect all.
		for (int i = 0; i < runner.level.armies.get(0).units.size(); i++)
		{
			runner.level.armies.get(0).units.get(i).selected = false;
		}
		//Select one.
		if (numSelecting < runner.level.armies.get(0).units.size())
			runner.level.armies.get(0).units.get(numSelecting).selected = true;*/
	}

}
