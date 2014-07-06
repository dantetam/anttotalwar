package info.comita.system;

import java.util.ArrayList;

import info.comita.data.Color;
import info.comita.data.Data;
import info.comita.level.Level;
import info.comita.render.*;
import processing.core.*;
import processing.event.MouseEvent;
import processing.opengl.PGL;

public class Runner extends PApplet {

	public UnitBar unitBar;

	public ArrayList<BaseSystem> systems;
	public RenderSystem renderSystem = new RenderSystem(this);
	public InputSystem inputSystem = new InputSystem(this);
	public CombatSystem combatSystem = new CombatSystem(this);
	public ThreadScheduler threadScheduler = new ThreadScheduler(this);

	public Level level;

	public static void main(String[] args)
	{
		PApplet.main(new String[] { info.comita.system.Runner.class.getName() });
	}

	public void setup()
	{
		size(1800,900,P3D);
		noCursor();
		frameRate(40);

		Data.setup();
		level = new Level();

		Data.addModel(0,getModel("ant1"));
		Data.addModel(1,getModel("ant1"));
		Data.addModel(2,getModel("ant2"));
		Data.addModel(3,getModel("ant3"));
		Data.addModel(4,getModel("ant4"));
		Data.addModel(5,getModel("ant5"));
		Data.addModel(99,getModel("banner"));
		
		systems = new ArrayList<BaseSystem>();
		systems.add(renderSystem);
		systems.add(inputSystem);
		systems.add(combatSystem);
		systems.add(threadScheduler);

		unitBar = new UnitBar(this);
		unitBar.init();
		unitBar.setBounds(100,700,1600,100);
		unitBar.setVisible(true);
		this.add(unitBar);
		
		Cursor cursor = new Cursor();
		cursor.init();
		cursor.setBounds(895,445,10,10);
		cursor.setVisible(true);
		this.add(cursor);
	}

	public void draw()
	{
		PGL pgl = beginPGL();
		pgl.enable(PGL.CULL_FACE);
		for (int i = 0; i < systems.size(); i++)
		{
			systems.get(i).tick();
		}
		endPGL();
	}

	public void mousePressed()
	{
		inputSystem.forwardMouse(mouseX, mouseY);
	}

	public void keyPressed()
	{
		if (key == 'w')
		{
			inputSystem.wPressed = true;
		}
		else if (key == 'a')
		{
			inputSystem.aPressed = true;
		}
		else if (key == 's')
		{
			inputSystem.sPressed = true;
		}
		else if (key == 'd')
		{
			inputSystem.dPressed = true;
		}
		inputSystem.forwardKey(key);
	}

	public void keyReleased()
	{
		if (key == 'w')
		{
			inputSystem.wPressed = false;
		}
		else if (key == 'a')
		{
			inputSystem.aPressed = false;
		}
		else if (key == 's')
		{
			inputSystem.sPressed = false;
		}
		else if (key == 'd')
		{
			inputSystem.dPressed = false;
		}
	}

	public void mouseWheel(MouseEvent event) {
		float e = event.getAmount();
		inputSystem.forwardMouse(e);
	}

	public ArrayList<Entity> getModel(String fileName)
	{	
		ArrayList<Entity> model = new ArrayList<Entity>();
		String[] temp = loadStrings("" + fileName);

		for (int i = 1; i < temp.length; i++) //first index is not part of data
		{
			if (!temp[i].startsWith("//"))
			{
				String[] stringData = split(temp[i],',');
				float[] data = new float[stringData.length];
				for (int j = 0; j < data.length - 1; j++) 
				{
					data[j] = Float.parseFloat(stringData[j]);
				}
				Entity en = new Entity();
				en.x = data[0];
				en.y = data[1];
				en.z = data[2];

				en.rotX = data[3];
				en.rotY = data[4];
				en.rotZ = data[5];

				en.sX = data[6];
				en.sY = data[7];
				en.sZ = data[8]; 

				if (data[9] == 1337)
				{
					en.r = -1; en.g = -1; en.b = -1;
				}
				else
				{
					Color c = Data.brickColorMap.get((int)data[9]);
					en.r = (float)c.r; en.g = (float)c.g; en.b = (float)c.b;
				}
				model.add(en);
			}
		}
		return model;
	}

}
