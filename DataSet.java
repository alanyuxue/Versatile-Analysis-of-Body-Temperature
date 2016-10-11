import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

class DataSet
{
	public int rate; //Sampling rate in minutes
	public int N; //number of data points
	public String[] times;
	public double[] values;
	
	public DataSet(File datafile)
	{
		try
		{
			Scanner s = new Scanner(datafile);
			//Read lines
			ArrayList<String> lines = new ArrayList<String>();
			while(s.hasNextLine())
				lines.add(s.nextLine());
			N = lines.size()-1;
			times = new String[N];
			values = new double[N];
			for(int i=0; i<N; i++)
			{
				String[] parts = lines.get(i+1).split(",");
				if(parts.length != 2)
				{
					//Bad file
				}
				times[i] = parts[0];
				values[i] = Double.parseDouble(parts[1]);
			}
			//Get sampling rate based off first 2 entries
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date date0 = null;
			Date date1 = null;
			try
			{
				date0 = format.parse(times[0]);
				date1 = format.parse(times[1]);
			}
			catch (Exception e)
			{
				
			}
			rate = (int) ((date1.getTime() - date0.getTime())/(1000*60));
			System.out.println("Rate: " +rate+" minutes between each sample");
		}
		catch (FileNotFoundException e)
		{
			
		}
	}
	
	/*public DataSet(File datafile)
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
	}*/
}