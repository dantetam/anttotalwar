package info.comita.system;

import java.util.ArrayList;

import info.comita.data.Data;
import info.comita.entity.*;

public class CombatSystem extends BaseSystem {

	public CombatSystem(Runner runner) {
		super(runner);
	}

	public void tick() {
		for (int i = 0; i < runner.level.armies.size(); i++)
		{
			for (int j = 0; j < runner.level.armies.get(i).units.size(); j++)
			{
				Unit u = runner.level.armies.get(i).units.get(j);
				u.speedModifier = 1;
				u.fighting = false;
				u.defenseModifier = 1;
				u.attackModifier = 1;
				u.outnumbered = false;

				if (u.charging)
				{
					u.speedModifier = 1.5;
					u.attackModifier = 2;
					u.defenseModifier = 0.8;
					if (u.category == 1 || u.category == 2)
					{
						//u.speedModifier = 1.7;
						u.attackModifier = 2.5;
						u.defenseModifier = 1;
					}
				}

				//System.out.println(intersects(u));

				ArrayList<Unit> inter = intersects(u);

				if (inter.size() > 0)
				{
					//System.out.println("fight");
					u.walkingThrough = true;
					u.speedModifier = 0.8;
					for (int k = 0; k < inter.size(); k++)
					{
						int enemies = 0; //check if being outnumbered
						if (inter.get(k).side != u.side)
						{
							if (!inter.get(k).routing)
								enemies++;
							u.fighting = true;
							if (Math.random() < 0.03)
							{
								u.charging = false;
							}
							u.speedModifier = 0.6;
							runner.threadScheduler.clearCommands(u,"movearmy");
							//System.out.println("fight");
						}
						if (u.retreating)
						{
							u.defenseModifier = 0.8;
							u.attackModifier = 0.2;
							//u.speedModifier = 0.2;
						}
						if (enemies >= 2)
						{
							u.defenseModifier = 1/enemies;
							u.outnumbered = true;
							u.speedModifier = 0.2;
						}
					}
				}
				else
				{
					u.walkingThrough = false;
					u.speedModifier = 1;
					u.attackModifier = 1;
					u.defenseModifier = 1;
				}

				int kills = 0;

				if (u.fighting) //Guaranteed at least one enemy unit.
				{
					/*if (Math.random() < 0.001)
					{
						//System.out.println("sss");
						runner.threadScheduler.commands.add(new Command("reform",new double[]{u.cX,u.cY,u.cZ},60,u,null));
					}*/
					for (int k = 0; k < inter.size(); k++)
					{
						//System.out.println("keee");
						Unit o = inter.get(k);
						double dif = u.attack*u.attackModifier - o.defense*o.defenseModifier;
						if (dif < 1) dif = 0.3;
						if (u.routing) continue;
						if (Math.random() < dif/100)
						{
							Ant ant = o.findClosestPoint(u);
							if (ant != null && inter.get(k).side != u.side)
							{
								kills++;
								//Calculate parabolic motion of arrow.
								double changeX = u.dist(u.cX, u.cZ, o.cX, o.cZ);
								double speedArrow = 2;
								double time = changeX/speedArrow;
								
								//Find an ant to shoot.
								Ant ant2 = u.occupants.get((int)(u.occupants.size()*Math.random()));
								
								double temp = Math.random()*20D/(double)time;
								Command arrow = new Command("parabolic",new double[]{u.cX + ant2.oX, u.cZ + ant2.oZ, (o.cX - u.cX)/time, (o.cZ - u.cZ)/time, time, u.cY, temp},(int)time,u,null);
								Command kill = new Command("delayedkill",new double[]{o.cX + ant.oX,o.cY + ant.oY,o.cZ + ant.oZ},(int)time,o,ant);
								runner.threadScheduler.delay(
										new Command("parabolicred",new double[]{u.cX + ant2.oX, u.cZ + ant2.oZ, (o.cX - u.cX)/time, (o.cZ - u.cZ)/time, time, u.cY, temp},(int)time,u,null)
										,1);
								runner.threadScheduler.delay(
										new Command("parabolicred",new double[]{u.cX + ant2.oX, u.cZ + ant2.oZ, (o.cX - u.cX)/time, (o.cZ - u.cZ)/time, time, u.cY, temp},(int)time,u,null)
										,2);
								//System.out.println(ant);
								Command blood = new Command("showblood",new double[]{},60,o,ant);
								arrow.chain(blood);
								runner.threadScheduler.commands.add(arrow);
								runner.threadScheduler.commands.add(kill);
								//runner.threadScheduler.commands.add(new Command("showblood",new double[]{ant.dX,ant.dY,ant.dZ},60,u));
								
								if (o.category == 1 || o.category == 2)
								{
									runner.threadScheduler.commands.add(new Command("gloriousdeath", new double[]{o.cX + ant2.oX, o.cZ + ant2.oZ},200,o,ant));
									runner.threadScheduler.commands.add(new Command("delayedkill",new double[]{o.cX + ant.oX,o.cY + ant.oY,o.cZ + ant.oZ},300,o,ant));
								}
							}
						}
					}
				}
				else if (!u.retreating)
				{

				}

				//Check for a rout every second
				if (runner.frameCount % 40 == 0)
				{
					if (u.morale < 3 && !u.routing)
					{
						u.routing = true; 
						u.attackModifier = 0;
						u.defenseModifier = 0.1;
						u.speedModifier = 1.2;
						if (u.selected) u.selected = false;
						double routX = Math.random()*500-250;
						double routZ = Math.random()*500-250;

						double dist = runner.dist((float)routX, (float)routZ, (float)u.cX, (float)u.cZ);
						int time = (int)(dist/(u.speed*u.speedModifier));

						//No command when routed
						runner.threadScheduler.clearCommands(u);
						runner.threadScheduler.commands.add(new Command(
								"rotate",new double[]{Math.atan2(routX - u.cX, routZ - u.cZ)},1,u,null
								));
						Command rout = new Command("rout",new double[]{routX/time,0,routZ/time},time*100,u,null);
						//rout.canCancel = false;
						runner.threadScheduler.commands.add(rout);
					}
				}
				//Check morale every 2.5 seconds
				if (runner.frameCount % 100 == 0)
				{
					u.reform();
					double moraleBefore = u.morale;
					int alive = (int)(u.occupants.size() - u.countDead()); //number of people alive this turn
					double suddenDeath = u.lastCount - alive; //number of people who died just now
					u.lastCount = alive;
					u.morale -= suddenDeath/u.occupants.size()*7D;
					if (u.fighting)
					{
						u.morale += (double)kills/(double)u.occupants.size()*2;
					}
					//System.out.println(suddenDeath/u.occupants.size()*8D);

					if (u.morale >= 5 && u.routing)
					{
						u.routing = false;
						u.attackModifier = 1;
						u.defenseModifier = 1;
						u.speedModifier = 1;
						runner.threadScheduler.clearAll(u);
					}
					double changeMorale = u.morale - moraleBefore;
					for (int k = 0; k < runner.level.armies.get(u.side).units.size(); k++)
					{
						Unit friendly = runner.level.armies.get(u.side).units.get(k);
						if (friendly.dist(friendly.cX, friendly.cZ, u.cX, u.cZ) < 200)
							friendly.morale += changeMorale/4;
					}
					if (u.morale > 0 && u.morale < Data.units.get(u.typeId).morale && changeMorale > -0.25) u.morale += 0.25;
					if (u.morale > Data.units.get(u.typeId).morale) u.morale = Data.units.get(u.typeId).morale;
					if (u.morale < 0) u.morale = 0;
				}

			}
		}
	}

	public ArrayList<Unit> intersects(Unit u)
	{
		ArrayList<Unit> returnThis = new ArrayList<Unit>();
		for (int i = 0; i < runner.level.armies.size(); i++)
		{
			for (int j = 0; j < runner.level.armies.get(i).units.size(); j++)
			{
				Unit o = runner.level.armies.get(i).units.get(j);
				//Don't check to see if the unit intersects with itself
				if (u.equals(o)) continue;
				//AABB collision
				if (u.cX + u.sX/2D < o.cX - o.sX/2D || 
						u.cZ + u.sY/2D < o.cZ - o.sY/2D ||  
						u.cX - u.sX/2D > o.cX + o.sX/2D || 
						u.cZ - u.sY/2D > o.cZ + o.sY/2D)
				{
					continue;
					//return returnThis;
				}
				else
				{
					returnThis.add(o);
				}
				//return true;
			}
		}
		return returnThis;
	}

}
