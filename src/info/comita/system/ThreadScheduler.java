package info.comita.system;

import info.comita.data.Data;
import info.comita.entity.Ant;
import info.comita.entity.Unit;

import java.util.ArrayList;

public class ThreadScheduler extends BaseSystem {

	public ArrayList<Command> commands;

	public ThreadScheduler(Runner runner) {
		super(runner);
		commands = new ArrayList<Command>();
	}

	public void tick() {
		for (int i = 0; i < commands.size(); i++)
		{
			Command com = commands.get(i);
			Unit u = com.obj;
			if (com.removeMe)
			{
				commands.remove(i);
				i--; //ArrayList trap
			};
			if (com.type.contains("movearmy"))
			{
				u.cX += com.data.get(0);
				u.cY += com.data.get(1);
				u.cZ += com.data.get(2);
				/*for (int j = 0; j < u.occupants.size(); j++)
				{
					if (Math.random() < 0.01)
					{
						u.occupants.get(j).oX += (int)(Math.random()*2 - 1);
						u.occupants.get(j).oZ += (int)(Math.random()*2 - 1);
					}
				}*/
			}
			else if (com.type.contains("chargearmy"))
			{
				u.charging = true;
				u.cX += com.data.get(0);
				u.cY += com.data.get(1);
				u.cZ += com.data.get(2);
			}
			else if (com.type.contains("rout"))
			{
				u.cX += com.data.get(0);
				u.cY += com.data.get(1);
				u.cZ += com.data.get(2);
			}
			else if (com.type.contains("retreatarmy"))
			{
				u.retreating = true;
				u.cX += com.data.get(0);
				u.cY += com.data.get(1);
				u.cZ += com.data.get(2);
			}
			else if (com.type.contains("rotate"))
			{
				for (int j = 0; j < u.occupants.size(); j++)
				{
					u.occupants.get(j).rot = com.data.get(0);
				}
			}
			else if (com.type.equals("parabolic"))
			{
				double oTime = com.data.get(4);
				runner.fill(0);
				if (com.obj.side == 0)
					runner.fill(0,0,255);
				else if (com.obj.side == 1)
					runner.fill(255,0,0);
				runner.pushMatrix();
				double translateY;
				if (oTime - com.duration <= oTime/2) //before halfway
				{
					translateY = com.data.get(6)*(oTime - com.duration);
				}
				else //after halfway
				{
					translateY = com.data.get(6)*(double)com.duration;
				}
				runner.translate(
						com.data.get(0).floatValue() + com.data.get(2).floatValue()*(float)(oTime-com.duration), 
						(float)translateY, 
						com.data.get(1).floatValue() + com.data.get(3).floatValue()*(float)(oTime-com.duration)
						);
				runner.box(1F/3F,1F/3F,1F/3F);
				runner.popMatrix();
			}
			else if (com.type.equals("parabolicred"))
			{
				double oTime = com.data.get(4);
				runner.fill(0,0,0);
				runner.pushMatrix();
				double translateY;
				if (oTime - com.duration <= oTime/2) //before halfway
				{
					translateY = com.data.get(6)*(oTime - com.duration);
				}
				else //after halfway
				{
					translateY = com.data.get(6)*(double)com.duration;
				}
				runner.translate(
						com.data.get(0).floatValue() + com.data.get(2).floatValue()*(float)(oTime-com.duration), 
						(float)translateY, 
						com.data.get(1).floatValue() + com.data.get(3).floatValue()*(float)(oTime-com.duration)
						);
				runner.box(1F/6F,1F/6F,1F/6F);
				runner.popMatrix();
			}
			else if (com.type.equals("delayedkill"))
			{
				//Late, only takes effect when complete
			}
			else if (com.type.equals("gloriousdeath"))
			{
				//Rotate 90 in the glorious death
				double theta = Math.PI/2D*((50D-(double)com.duration)/50D);
				com.obj2.theta = theta;
			}
			else if (com.type.contains("showblock"))
			{
				runner.fill(0);
				runner.pushMatrix();
				runner.translate(com.data.get(0).floatValue(), com.data.get(1).floatValue(), com.data.get(2).floatValue());
				runner.box((float)com.duration/10F,(float)com.duration/10F,(float)com.duration/10F);
				runner.popMatrix();
			}
			else if (com.type.contains("showblood"))
			{
				runner.fill(255,0,0);
				runner.pushMatrix();
				runner.translate((float)com.obj2.dX, (float)com.obj2.dY, (float)com.obj2.dZ);
				runner.box((float)com.duration/15F,0.5F,(float)com.duration/15F);
				runner.popMatrix();
			}
			else if (com.type.contains("reform"))
			{
				if (com.otherData.size() == 0)
				{
					//System.out.println("sssss");
					double dead = u.countDead();
					u.sX *= 1-dead/(dead+u.occupants.size());
					u.sY *= 1-dead/(dead+u.occupants.size());
					for (int j = 0; j < u.occupants.size(); j++)
					{
						Ant ant = u.occupants.get(j);
						com.otherData.add(new double[]{
								-(ant.sX - ant.sX/2 + ant.oX)/(double)com.duration/10,
								0,
								-(ant.sY - ant.sY/2 + ant.oZ)/(double)com.duration/10}
								);
						/*System.out.println(new double[]{
								((Math.random()*ant.sX - ant.sX/2)-ant.oX)/(double)com.duration,
								0,
								((Math.random()*ant.sY - ant.sY/2)-ant.oZ)/(double)com.duration});*/
					}
					u.reform();
				}
				else
				{
					for (int j = 0; j < u.occupants.size(); j++)
					{
						Ant ant = u.occupants.get(j);
						double[] data = com.otherData.get(j);
						ant.oX += data[0];
						//System.out.println(data[0] + " " + data[2]);
						//ant.oY
						ant.oZ += data[2];
					}
				}
			}
			if (i == -1) return;
			if (com.duration-- <= 0)
			{
				if (com.getChain() != null)
				{
					commands.add(com.getChain());
					com.chain(null);
				}
				if (com.type.equals("retreatarmy"))
					com.obj.retreating = false;
				else if (com.type.equals("chargearmy"))
					com.obj.charging = false;
				else if (com.type.equals("delayedkill"))
				{
					com.obj2.die(u);
					com.obj2.dX = com.data.get(0);
					com.obj2.dY = com.data.get(1);
					com.obj2.dZ = com.data.get(2);
				}
				Command removing = commands.get(i);
				commands.remove(i);
				removing = null;
				i--; //ArrayList trap
			};
		}
		for (int i = 0; i < delayedCommands.size(); i++)
		{
			if (runner.frameCount >= desiredFrames.get(i))
			{
				desiredFrames.remove(i);
				commands.add(delayedCommands.get(i));
				delayedCommands.remove(i);
				i--;
			}
		}
	}
	
	public ArrayList<Command> delayedCommands = new ArrayList<Command>(); //list of commands that aren't executing yet
	public ArrayList<Integer> desiredFrames = new ArrayList<Integer>(); //respectively, the frame that each command waits for
	public void delay(Command command, int byFrames)
	{
		delayedCommands.add(command);
		desiredFrames.add(runner.frameCount + byFrames);
	}

	//Clears everything except routs
	public void clearCommands(Unit u)
	{
		for (int i = 0; i < commands.size(); i++)
		{
			if (commands.get(i).obj.equals(u) && commands.get(i).canCancel)
			{
				commands.get(i).removeMe = true;
			}
		}
	}

	//Clears every command of type s
	public void clearCommands(Unit u, String s)
	{
		for (int i = 0; i < commands.size(); i++)
		{
			if (commands.get(i).obj.equals(u) && commands.get(i).type.equals(s) && commands.get(i).canCancel)
			{
				commands.get(i).removeMe = true;
			}
		}
	}

	//Clears everything
	public void clearAll(Unit u)
	{
		for (int i = 0; i < commands.size(); i++)
		{
			if (commands.get(i).obj.equals(u))
			{
				commands.get(i).removeMe = true;
			}
		}
	}

}
