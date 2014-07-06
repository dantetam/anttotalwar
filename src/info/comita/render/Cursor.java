package info.comita.render;

import processing.core.PApplet;

public class Cursor extends PApplet {

	public Cursor()
	{
		
	}
	
	public void setup()
	{
		noLoop();
		stroke(0);
		fill(255,0,255);
	}
	
	public void draw()
	{
		background(255,0,255);
		rect(0,0,10,10);
	}
	
}
