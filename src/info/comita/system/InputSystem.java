package info.comita.system;

import info.comita.entity.Unit;
import info.comita.math.*;

public class InputSystem extends BaseSystem {

	//public float pMouseX = 900;
	//public float pMouseY = 450;
	//public float mouseX, mouseY;

	public InputSystem(Runner runner) {
		super(runner);
	}

	public void tick()
	{
		float mouseX = runner.mouseX;
		float mouseY = runner.mouseY;
		//float dX = mouseX - pMouseX;
		//float dY = mouseY - pMouseY;
		calculateCamera(mouseX, mouseY);
		forwardUnitTip(mouseX, mouseY);
	}

	public boolean wPressed, aPressed, sPressed, dPressed = false;
	public boolean guiVisible = true;
	public void forwardKey(char key)
	{
		if (Character.isDigit(key) && guiVisible)
		{
			int numSelecting = key - 48 - 1;
			//Deselect all.
			for (int i = 0; i < runner.level.armies.get(0).units.size(); i++)
			{
				runner.level.armies.get(0).units.get(i).selected = false;
			}
			//Select one.
			if (!(numSelecting >= runner.level.armies.get(0).units.size()))
			{
				Unit selecting = runner.level.armies.get(0).units.get(numSelecting);
				if (numSelecting < runner.level.armies.get(0).units.size() && numSelecting >= 0 && selecting.countDead() != selecting.occupants.size())
					if (!selecting.routing)
						selecting.selected = true;
			}
		}
		else if (key == 8)
		{
			for (int i = 0; i < runner.level.armies.get(0).units.size(); i++)
			{
				Unit u = runner.level.armies.get(0).units.get(i);
				if (u.selected)
				{
					runner.threadScheduler.clearCommands(u);
					runner.threadScheduler.commands.add(new Command("reform",new double[]{u.cX,u.cY,u.cZ},60,u,null));
					break;
				}
			}
		}
		else if (key == 'i')
		{
			guiVisible = !guiVisible;
			runner.unitBar.setVisible(guiVisible);
		}
		/*float p = 8; //Pan by this much
		float[] cA = runner.renderSystem.cA;*/
	}

	//Forward the mouse wheel.
	public void forwardMouse(float e)
	{
		runner.renderSystem.cA[1] += e*5;
		runner.renderSystem.cA[4] += e*5;
	}

	public void forwardMouse(float x, float y)
	{
		//Look for selected units
		for (int i = 0; i < runner.level.armies.get(0).units.size(); i++)
		{
			if (runner.level.armies.get(0).units.get(i).selected)
			{
				Unit u = runner.level.armies.get(0).units.get(i);
				float[] cA = runner.renderSystem.cA;

				if (u.routing) continue;

				//Find what the player is looking at.
				Line l = new Line(cA[3]-cA[0],cA[0],cA[4]-cA[1],cA[1],cA[5]-cA[2],cA[2]);
				Plane p = new Plane(0,1,0,0);
				Point point = p.intersect(l);

				//System.out.println(point);

				double dist = runner.dist((float)point.x, (float)point.z, (float)u.cX, (float)u.cZ);

				int time = (int)(dist/(u.speed*u.speedModifier));
				//System.out.println(dist + " " + u.speed);

				//Cancel previous orders.
				/*for (int j = 0; j < runner.threadScheduler.commands.size(); j++)
				{
					Command com = runner.threadScheduler.commands.get(j);
					if (com.obj.equals(u) && com.type.equals("movearmy"))
					{
						com.removeMe = true;
					}
				}*/

				runner.threadScheduler.clearCommands(u);

				//Rotate and walk.
				runner.threadScheduler.commands.add(new Command(
						"showblock",new double[]{point.x, point.y, point.z},50,u,null
						));
				runner.threadScheduler.commands.add(new Command(
						"rotate",new double[]{Math.atan2(point.x - u.cX, point.z - u.cZ)},1,u,null
						));
				if (!u.fighting)
				{
					//See if player is attempting to charge an enemy unit.
					if (runner.unitBar.ctt == null)
					{
						runner.threadScheduler.commands.add(new Command(
								"movearmy",new double[]{(point.x - u.cX)/time,0,(point.z- u.cZ)/time},time,u,null
								));
					}
					else
					{
						runner.threadScheduler.commands.add(new Command(
								"chargearmy",new double[]{(point.x - u.cX)/time,0,(point.z- u.cZ)/time},time,u,null
								));
					}
				}
				else
					runner.threadScheduler.commands.add(new Command(
							"retreatarmy",new double[]{(point.x - u.cX)/time,0,(point.z- u.cZ)/time},time,u,null
							));
			}
		}

	}

	//Display a tooltip for an enemy unit.
	public void forwardUnitTip(float mouseX, float mouseY)
	{
		float[] cA = runner.renderSystem.cA;
		Line l = new Line(cA[3]-cA[0],cA[0],cA[4]-cA[1],cA[1],cA[5]-cA[2],cA[2]);
		Plane p = new Plane(0,1,0,0);
		Point point = p.intersect(l);

		for (int i = 1; i < runner.level.armies.size(); i++)
		{
			for (int j = 0; j < runner.level.armies.get(i).units.size(); j++)
			{
				Unit u = runner.level.armies.get(i).units.get(j);
				if (point.x > u.cX - u.sX/2 && point.z > u.cZ - u.sY/2 && point.x < u.cX + u.sX/2 && point.z < u.cZ + u.sY/2)
				{
					runner.unitBar.forwardUnitTip(u);
					return;
				}
			}
		}
		runner.unitBar.forwardUnitTip(null);
	}

	public float aRotY = 0; //angle of rotation on the y axis
	public float aRotX = 0;
	public float sight = 10;
	public float lastMouseX = runner.width/2;
	public float lastMouseY = runner.height/2;
	//Pan camera
	public void calculateCamera(float x, float y)
	{
		float p = 2; //Pan by this much
		float[] cA = runner.renderSystem.cA;
		/*if (x > runner.width*9F/10F)
		{
			cA[0] -= p;
			cA[3] -= p;
		}
		else if (x < runner.width/10F)
		{
			cA[0] += p;
			cA[3] += p;
		}
		if (y > runner.height*9F/10F)
		{
			cA[2] += p;
			cA[5] += p;
		}
		else if (y < runner.height/10F)
		{
			cA[2] -= p;
			cA[5] -= p;
		}*/
		double difX = x - lastMouseX;
		double difY = y - lastMouseY; //do nothing for now
		lastMouseX = (float)x;
		lastMouseY = (float)y;
		//robot.mouseMove(p.width/2, p.height/2);

		//robot.mouseMove((int)(mouseX + p.width/2D),(int)(mouseY + p.height/2D));

		aRotY -= difX/runner.width*4*Math.PI;
		aRotX += difY/runner.height*2*Math.PI;
		cA[3] = cA[0] + sight*(float)Math.cos(aRotY);
		cA[4] = cA[1] + sight*(float)Math.sin(aRotX);
		cA[5] = cA[2] + sight*(float)Math.sin(aRotY); //- (float)(sight*Math.sin(aRotX)*Math.cos(Math.PI/2-aRotX/2));

		p = 5;
		/*if (wPressed)
		{
			cA[2] -= p;
			cA[5] -= p;
		}
		else if (aPressed)
		{
			cA[0] += p;
			cA[3] += p;
		}
		else if (sPressed)
		{
			cA[2] += p;
			cA[5] += p;
		}
		else if (dPressed)
		{
			cA[0] -= p;
			cA[3] -= p;
		}*/

		if (wPressed)
		{
			cA[0] += p*(float)Math.cos(aRotY);
			cA[2] += p*(float)Math.sin(aRotY);
			cA[3] += p*(float)Math.cos(aRotY);
			cA[5] += p*(float)Math.sin(aRotY);
		}
		if (sPressed)
		{
			cA[0] -= p*(float)Math.cos(aRotY);
			cA[2] -= p*(float)Math.sin(aRotY);
			cA[3] -= p*(float)Math.cos(aRotY);
			cA[5] -= p*(float)Math.sin(aRotY);
		}
		if (aPressed)
		{
			cA[0] -= p*(float)Math.cos(aRotY-Math.PI/2);
			cA[2] -= p*(float)Math.sin(aRotY-Math.PI/2);
			cA[3] -= p*(float)Math.cos(aRotY-Math.PI/2);
			cA[5] -= p*(float)Math.sin(aRotY-Math.PI/2);
		}
		if (dPressed)
		{
			cA[0] -= p*(float)Math.cos(aRotY+Math.PI/2);
			cA[2] -= p*(float)Math.sin(aRotY+Math.PI/2);
			cA[3] -= p*(float)Math.cos(aRotY+Math.PI/2);
			cA[5] -= p*(float)Math.sin(aRotY+Math.PI/2);
		}
		//aRotY %= 360; aRotX %= 360;

	}

}
