package info.comita.system;

import java.util.ArrayList;

import info.comita.data.Data;
import info.comita.entity.*;
import info.comita.render.Entity;

public class RenderSystem extends BaseSystem {

	public float[] cA = {0,100,100,0,0,0,0,-1,0}; //camera array

	public RenderSystem(Runner runner) {
		super(runner);
	}

	public void tick() 
	{
		runner.background(173, 216, 230);
		//runner.lights();
		//runner.ambient(255);
		float fov = (float) (Math.PI/3.0);
		float cameraZ = (float) ((runner.height/2.0) / Math.tan(fov/2.0));
		runner.perspective(fov, (float)runner.width/(float)runner.height, cameraZ/100F, cameraZ*100F);
		
		//runner.background(20,132,224);
		runner.background(173, 216, 230);

		runner.camera(cA[0], cA[1], cA[2], cA[3], cA[4], cA[5], cA[6], cA[7], cA[8]);
		
		/*runner.pushMatrix();
		runner.translate((float)cA[3], (float)cA[4], (float)cA[5]);
		runner.fill(255,0,0);
		runner.box(5,1,5);
		runner.popMatrix();*/

		for (int i = 0; i < runner.level.terrain.size(); i++)
		{
			runner.noStroke();
			Entity en = runner.level.terrain.get(i);
			double dist = runner.dist(cA[0], cA[2], (float)en.x, (float)en.z);
			if (dist < 200)
			{
				runner.stroke(0);
			}
			else if (dist > 300)
			{
				continue;
			}
			runner.pushMatrix();
			runner.fill(en.r, en.g, en.b);
			runner.translate(en.x, en.y, en.z);
			runner.rotateX(en.rotX);
			runner.rotateY(en.rotY);
			runner.rotateZ(en.rotZ);
			runner.box(en.sX, en.sY, en.sZ);
			runner.popMatrix();
		}

		for (int i = 0; i < runner.level.armies.size(); i++)
		{
			Army a = runner.level.armies.get(i);
			for (int j = 0; j < a.units.size(); j++)
			{
				Unit u = a.units.get(j);
				runner.pushMatrix();
				runner.translate((float)u.cX, (float)u.cY, (float)u.cZ);
				ArrayList<Entity> flag = Data.models.get(99);
				for (int part = 0; part < flag.size(); part++)
				{
					runner.strokeWeight(1);
					runner.stroke(0);
					if (u.selected)
					{
						runner.strokeWeight(4);
						runner.stroke(0,0,255);
					}
					Entity en = flag.get(part);
					runner.pushMatrix();
					if (u.routing)
					{
						if (u.side == 0)
							runner.fill(150,150,255);
						else if (u.side == 1)
							runner.fill(255,200,200);
					}
					else if (en.r == -1 || en.g == -1 || en.b == -1)
					{
						if (u.side == 0)
							runner.fill(0, 0, 255);
						else if (u.side == 1)
							runner.fill(255, 0, 0);
						else if (u.side == 2)
							runner.fill(0, 200, 200);
					}
					else
						runner.fill(en.r, en.g, en.b);
					runner.strokeWeight(1);
					runner.translate(en.x, en.y, en.z);
					//runner.rotateZ((float)ant.theta);
					runner.rotateX(en.rotX);
					runner.rotateY(en.rotY);
					runner.rotateZ(en.rotZ);
					runner.box(en.sX, en.sY, en.sZ);
					runner.popMatrix();
				}

				runner.popMatrix();
				for (int k = 0; k < u.occupants.size(); k++)
				{
					runner.fill(0);
					Ant ant = u.occupants.get(k);
					runner.pushMatrix();
					if (u.selected) runner.stroke(0,0,255);
					else runner.stroke(0,0,0);
					double dist = runner.dist((float)u.cX, (float)u.cZ, cA[0], cA[2]);
					if (ant.dead)
					{
						runner.translate((float)ant.dX,(float)ant.dY,(float)ant.dZ);
						runner.fill(100);
						runner.noStroke();
						runner.rotateZ((float)Math.PI/2);
						runner.translate(0,-ant.sY/2,0);
						runner.box((float)ant.sX, (float)ant.sY, (float)ant.sZ);
					}
					else if (dist > 200 && dist < 300)
					{
						runner.translate((float)(u.cX + ant.oX), (float)(u.cY + ant.oY), (float)(u.cZ + ant.oZ));
						runner.rotateY((float)ant.rot);
						runner.box((float)ant.sX, (float)ant.sY, (float)ant.sZ);
					}
					else if (dist < 200)
					{
						/*runner.translate((float)(u.cX + ant.oX), (float)(u.cY + ant.oY), (float)(u.cZ + ant.oZ));
						runner.rotateY((float)ant.rot);
						runner.box((float)ant.sX, (float)ant.sY, (float)ant.sZ);*/


						runner.translate((float)(u.cX + ant.oX), (float)(u.cY + ant.oY), (float)(u.cZ + ant.oZ));
						ArrayList<Entity> model = Data.models.get(u.typeId);

						//System.out.println(model);
						runner.scale(1F/3F);
						runner.rotateY((float)u.occupants.get(0).rot);

						runner.rotateZ((float)ant.theta);

						//System.out.println("yeee");
						for (int part = 0; part < model.size(); part++)
						{
							//System.out.println("yeee");
							runner.pushMatrix();
							Entity en = model.get(part);
							if (en.r == -1 || en.g == -1 || en.b == -1)
							{
								if (u.side == 0)
									runner.fill(0, 0, 255);
								else if (u.side == 1)
									runner.fill(255, 0, 0);
								else if (u.side == 2)
									runner.fill(0, 200, 200);
							}
							else
								runner.fill(en.r, en.g, en.b);
							runner.translate(en.x, en.y, en.z);
							//runner.rotateZ((float)ant.theta);
							runner.rotateX(en.rotX);
							runner.rotateY(en.rotY);
							runner.rotateZ(en.rotZ);
							runner.box(en.sX, en.sY, en.sZ);
							runner.popMatrix();
						}

					}
					runner.popMatrix();
				}
			}
		}
		runner.unitBar.redraw();
	}

}
