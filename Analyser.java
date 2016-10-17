import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

class Analyser
{
	public DataSet dset;
	
	public Analyser(DataSet ds)
	{
		dset = ds;
	}
	
	public double getPeriod(int start, int end)
	{
		int N = end-start+1;
		double[] reals = Arrays.copyOfRange(dset.values,start,end+1);
		double[] ims = new double[N];
		FFT.transform(reals,ims);
		double[] result = new double[N];
		for(int i=0; i<N; i++)
		{
			result[i] = Math.sqrt(reals[i]*reals[i]+ims[i]*ims[i])/N;
		}
		double max = 0;
		double maxid = 0;
		for(int i=1; i<N/2+1; i++)
		{
			if(result[i] > 0 && result[N-i] > 0)
			{
				if(result[i]+result[N-1] > max)
				{
					max = result[i]+result[N-1];
					maxid = i;
				}
			}
		}

		return ((start+N)/maxid)*dset.rate;
	}
	
	public Cosine doCosinor(double period, int start, int end)
	{
		int N = end-start+1;
		double[] values = Arrays.copyOfRange(dset.values, start, end+1);
		double[] times = new double[N];
		for(int i=0; i<N; i++)
			times[i] = dset.rate*i;
		return Cosinor.solve(times,values,period);
	}
	
	public double getMSR(Cosine curve)
	{
		int N = dset.N;
		double sum = 0;
		for(int i=0; i<N; i++)
		{
			double dif = curve.getValue(i*dset.rate)-dset.values[i];
			sum += dif*dif;
		}
		return sum/N;
	}
	
	public ArrayList<Integer> getOutliers(int start, int end, Cosine curve, double thresh)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=start; i<=end; i++)
		{
			double dif = Math.abs(curve.getValue(i*dset.rate)-dset.values[i]);
			if(dif > thresh*curve.getAmplitude())
				list.add(i);
		}
		return list;
	}
	
	public ArrayList<String> outlierRanges(int start, int end, Cosine curve, double thresh)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		ArrayList<String> strs = new ArrayList<String>();
		ArrayList<Integer> outliers = getOutliers(start,end,curve,thresh);
		int N = end-start+1;
		boolean[] isOutlier = new boolean[N];
		for(int i : outliers)
		{
			isOutlier[i] = true;
		}
		int i=start;
		int rstart = -1;
		while(i <= end)
		{
			if(isOutlier[i-start])
			{
				if(rstart == -1)
					rstart = i;
			}
			else
			{
				if(rstart != -1)
				{
					if(rstart == i-1)
						strs.add(format.format(indexToDate(rstart)));
					else
						strs.add(format.format(indexToDate(rstart))+"-"+format.format(indexToDate(i-1)));
				}
				rstart = -1;
			}
			i++;
		}
		if(rstart != -1)
		{
			if(rstart == i-1)
				strs.add(format.format(indexToDate(rstart)));
			else
				strs.add(format.format(indexToDate(rstart))+"-"+format.format(indexToDate(i-1)));
		}
		rstart = -1;
		return strs;
	}
	
	public Date indexToDate(int i)
	{
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(dset.times[0]);
		 cal.add(Calendar.MINUTE, i*dset.rate);
		 return cal.getTime();
	}
	
	public int dateToIndex(Date d)
	{
		return (int) (d.getTime()-dset.times[0].getTime())/dset.rate;
	}
	
	public ArrayList<String> reportStrings(Cosine wave)
	{
		ArrayList<String> str = new ArrayList<String>();
		str.add("Results for "+dset.name);
		str.add("Period: "+wave.getPeriod()+" minutes ("+(wave.getPeriod()/60)+" hours)");
		str.add("MESOR: "+wave.getMESOR());
		str.add("Amplitude: "+wave.getAmplitude());
		str.add("Acrophase: "+wave.getAcrophase()+" minutes");
		ArrayList<String> outliers = outlierRanges(0,dset.N-1,wave,2);
		str.add("Outliers: ");
		for(String s : outliers)
			str.add(s);
		return str;
	}
	
	public boolean createReport(Cosine wave)
	{
		try
		{
			PrintWriter writer = new PrintWriter(dset.path+" - REPORT.txt", "UTF-8");
			ArrayList<String> lines = reportStrings(wave);
			for(String str : lines)
			{
				writer.println(str);
			}
			writer.close();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public double getValueFromDate(Date d, Cosine wave)
	{
		return wave.getValue((d.getTime()-dset.startDate.getTime())/(60*1000));
	}
	
	public ArrayList<Date> fittedDates()
	{
		ArrayList<Date> dates = new ArrayList<Date>();
		int i = dateToIndex(dset.startDate);
		int j = dateToIndex(dset.endDate);
		for(int k=i; k<=j; k++)
		{
			dates.add(dset.times[k]);
		}
		return dates;
	}
	
	public ArrayList<Double> fittedValues(Cosine wave)
	{
		ArrayList<Double> values = new ArrayList<Double>();
		int i = dateToIndex(dset.startDate);
		int j = dateToIndex(dset.endDate);
		for(int k=i; k<=j; k++)
		{
			values.add(getValueFromDate(dset.times[k],wave));
		}
		return values;
	}

}