package info.comita.entity;

public class ArmyConstructor {

	public Army army;
	
	public ArmyConstructor(Army army, int[] types, int cX, int cY, int cZ, int side)
	{
		this.army = army;
		for (int i = 0; i < types.length; i++)
		{
			addUnit(types[i],side);
		}
		formUp(cX, cY, cZ);
	}
	
	public Unit addUnit(int typeId, int side)
	{
		Unit u = new Unit(typeId);
		
		double rot = 0;
		if (side == 1)
			rot = 0;
		else if (side == 0)
			rot = Math.PI;
		for (int i = 0; i < u.occupants.size(); i++)
			u.occupants.get(i).rot = rot;
		
		u.side = side;
		army.units.add(u);
		return u;
	}
	
	public void formUp(int cX, int cY, int cZ)
	{
		int currentX = cX;
		for (int i = 0; i < army.units.size(); i++)
		{
			Unit unit = army.units.get(i);
			unit.moveTo(cX, cY, cZ);
			unit.move(currentX, 0, 0);
			currentX += unit.sX + 5;
			//System.out.println(currentX);
		}
	}
	
}
