import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

class DataSet
{
	public String path;
	public String name;
	public int rate; //Sampling rate in minutes
	public int N; //number of data points
	public Date[] times;
	public double[] values;
	public Date startDate;
	public Date endDate;
	
	public DataSet(File datafile) throws Exception
	{
		{
			Scanner s = new Scanner(datafile);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			path = datafile.getPath();
			name = datafile.getName();
			//Read lines
			ArrayList<String> lines = new ArrayList<String>();
			while(s.hasNextLine())
				lines.add(s.nextLine());
			N = lines.size()-1;
			times = new Date[N];
			values = new double[N];
			for(int i=0; i<N; i++)
			{
				String[] parts = lines.get(i+1).split(",");
				if(parts.length != 2)
				{
					//Bad file
				}
				//try
				{
					times[i] = format.parse(parts[0]);
				}
				values[i] = Double.parseDouble(parts[1]);
			}
			//Get sampling rate based off first 2 entries
			rate = (int) ((times[1].getTime()-times[0].getTime())/(1000*60));
			
			int samplesperday=1440/rate;
			startDate = times[0];
			endDate = new Date(times[samplesperday*(Math.floorDiv(N, samplesperday))-1].getTime()+rate*60*1000);
			s.close();
		}
	}
}