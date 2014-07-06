package info.comita.math;

import java.util.Random;

//Generates perlin noise. Limited.

public class PerlinNoise {

	private Random rand;
	
	public PerlinNoise(long seed)
	{
		rand = new Random(seed);
	}
	
	public static void main(String[] args)
	{
		double[][] oldSource = new PerlinNoise(870691).makePerlinNoise(16,16,3,8,3,0.5,2);
		double[][] source = PerlinNoise.recurInter(oldSource,4,4);
		for (int i = 0; i < source.length; i++)
		{
			for (int j = 0; j < source[0].length; j++)
			{
				System.out.print((int)source[i][j] + " ");
			}
			System.out.println("");
		}
		//return newSource;
	}

	public double[][] makePerlinNoise(int width, int height, double startAmp, double startFreq, double persistence, double ampFreqRatio, int times)
	{
		double[][][] averageLater = new double[times][width][height];
		for (int i = 0; i < times; i++)
		{
			//averageLater[i] = recurInter(generateNoise(width,height,startAmp*Math.pow(ampFreqRatio, i),(int)(startFreq/Math.pow(ampFreqRatio, i))),1,width/2);
			averageLater[i] = generateNoise(width,height,startAmp*Math.pow(ampFreqRatio, i),(int)(startFreq/Math.pow(ampFreqRatio, i)));		
		}
		double[][] b = averageTables(persistence,averageLater);
		b = positiveTable(b);
		return b;
	}
	
	public static double[][] recurInter(double[][] source, int times, double nDiv)
	{
		if (times < 0)
		{
			return source;
		}
		return recurInter(expand(source,nDiv),times-1,nDiv*2);
	}
	
	public static double[][] expand(double[][] a, double nDiv)
	{
		BicubicInterpolator bi = new BicubicInterpolator();
		double[][] returnThis = new double[(int)nDiv][(int)nDiv];
		for (int i = 0; i < nDiv; i++)
		{
			for (int j = 0; j < nDiv; j++)
			{
				double idx = (double)(a.length*i)/nDiv;
				double idy = (double)(a[0].length*j)/nDiv;
				//System.out.println("L: " + idx + "," + idy + ": " + bi.getValue(source,idx,idx));
				double zeroCheck = bi.getValue(a,idx,idy);
				returnThis[i][j] = zeroCheck >= 0 ? zeroCheck : 0;
			}
		}
		return returnThis;
	}
	
	public static int[][] toInt(double[][] a)
	{
		int[][] returnThis = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				returnThis[i][j] = (int)a[i][j];
			}
		}
		return returnThis;
	}

	public static class CubicInterpolator {
		public static double getValue(double[] p, double x) {
			int xi = (int) x;
			x -= xi;
			double p0 = p[Math.max(0, xi - 1)];
			double p1 = p[xi];
			double p2 = p[Math.min(p.length - 1,xi + 1)];
			double p3 = p[Math.min(p.length - 1, xi + 2)];
			return p1 + 0.5 * x * (p2 - p0 + x * (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3 + x * (3.0 * (p1 - p2) + p3 - p0)));
		}
	}

	public static class BicubicInterpolator extends CubicInterpolator {
		private double[] arr = new double[4];

		public double getValue(double[][] p, double x, double y) {
			int xi = (int) x;
			x -= xi;
			arr[0] = getValue(p[Math.max(0, xi - 1)], y);
			arr[1] = getValue(p[xi], y);
			arr[2] = getValue(p[Math.min(p.length - 1,xi + 1)], y);
			arr[3] = getValue(p[Math.min(p.length - 1, xi + 2)], y);
			return getValue(arr, x+ 1);
		}
	}

	public double[][] positiveTable(double[][] a)
	{
		double[][] b = new double[a.length][a[0].length];
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				b[i][j] = Math.abs(a[i][j]);
			}
		}
		return b;
	}
	
	public double[][] scalar(double ratio, double[][] a)
	{
		for (int i = 0; i < a.length; i++)
		{
			for (int j = 0; j < a[0].length; j++)
			{
				a[i][j] *= ratio;
			}
		}
		return a;
	}

	public double[][] averageTables(double ratio, double[][][] a)
	{
		double[][] returnThis = new double[a[0].length][a[0][0].length];
		for (int tNum = 0; tNum < a.length; tNum++)
		{
			//System.out.println("kek");
			for (int i = 0; i < a[0].length; i++)
			{
				for (int j = 0; j < a[0][0].length; j++)
				{
					returnThis[i][j] += a[tNum][i][j]*(1/Math.pow(ratio, tNum-1));
				}
			}
		}
		return returnThis;
	}

	public double[][] generateNoise(int width, int height, double amp, int freq)
	{
		double[][] returnThis = new double[width][height];
		for (int i = 0; i < width; i += width/freq)
		{
			for (int j = 0; j < height; j += height/freq)
			{
				fillPatch(returnThis,i,j,width/freq,height/freq,rand.nextDouble()*amp - amp/2);
			}
		}
		return returnThis;
	}

	public void fillPatch(double[][] arr, int a, int b, int w, int h, double number)
	{
		for (int i = a; i < a+w; i++)
		{
			for (int j = b; j < b+w; j++)
			{
				arr[i][j] = number;
			}
		}
	}

	public double dist(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
	}

}
