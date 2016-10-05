import java.util.*;
import java.io.*;

class DataSet
{
	public int rate; //Sampling rate in minutes
	public int N; //number of data points
	public double[] data;
	
	public DataSet(File datafile)
	{
		try
		{
			Scanner s = new Scanner(datafile);
			rate = s.nextInt();
			N = s.nextInt();
			data = new double[N];
			for(int i=0; i<N; i++)
			{
				data[i] = s.nextDouble();
			}
		}
		catch (FileNotFoundException e)
		{
			
		}
	}
}