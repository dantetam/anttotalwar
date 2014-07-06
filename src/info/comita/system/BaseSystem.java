package info.comita.system;

public abstract class BaseSystem {

	public Runner runner;
	
	public BaseSystem(Runner runner)
	{
		this.runner = runner;
	}
	
	public abstract void tick();
	
}
