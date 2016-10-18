import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

class Analyser
{
	public DataSet dset;
	public ArrayList<Integer> outliers;
	public double outlierSense = 2.0;
	
	public Analyser(DataSet ds)
	{
		dset = ds;
	}
	
	public double getPeriod(int start, int end)
	{
		int N = end-start;
		double[] reals = Arrays.copyOfRange(dset.values,start,end);
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

		return ((N)/maxid)*dset.rate;
	}
	
	public Cosine doCosinor(double period, int start, int end)
	{
		int N = end-start;
		double[] values = Arrays.copyOfRange(dset.values, start, end);
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
	
	public void getOutliers(int start, int end, Cosine curve, double thresh)
	{
		outlierSense = thresh;
		outliers = new ArrayList<Integer>();
		for(int i=start; i<end && i<dset.values.length; i++)
		{
			double dif = Math.abs(curve.getValue(i*dset.rate)-dset.values[i]);
			if(dif > thresh*curve.getAmplitude())
				outliers.add(i);
		}
	}
	
	public ArrayList<String> outlierRanges(int start, int end, Cosine curve, double thresh)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		ArrayList<String> strs = new ArrayList<String>();
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
		return (int) ((d.getTime()-dset.startDate.getTime())/(dset.rate*60*1000));
	}
	
	public ArrayList<String> reportStrings(Date s, Date e, Cosine wave)
	{
		ArrayList<String> str = new ArrayList<String>();
		str.add("Results for "+dset.name);
		str.add("Period: "+wave.getPeriod()+" minutes ("+(wave.getPeriod()/60)+" hours)");
		str.add("MESOR: "+wave.getMESOR());
		str.add("Amplitude: "+wave.getAmplitude());
		str.add("Acrophase: "+wave.getAcrophase()+" minutes");
		ArrayList<String> outliers = outlierRanges(dateToIndex(s),dateToIndex(e),wave,2);
		str.add("Outliers: (Sensitivity = "+outlierSense+"): ");
		for(String c : outliers)
			str.add(c);
		return str;
	}
	
	public String createReport(Date s, Date e, Cosine wave)
	{
		try
		{
			PrintWriter writer = new PrintWriter(dset.path+" - REPORT.txt", "UTF-8");
			ArrayList<String> lines = reportStrings(s,e,wave);
			for(String str : lines)
			{
				writer.println(str);
			}
			writer.close();
			return dset.path+" - REPORT.txt";
		}
		catch(Exception exc)
		{
			return "";
		}
	}
	
	public double getValueFromDate(Date d, Cosine wave)
	{
		return wave.getValue((d.getTime()-dset.startDate.getTime())/(60*1000));
	}
	
	public ArrayList<Date> fittedDates(Date s, Date e)
	{
		ArrayList<Date> dates = new ArrayList<Date>();
		int i = dateToIndex(s);
		int j = dateToIndex(e);
		for(int k=i; k<j && k<dset.times.length; k++)
		{
			dates.add(dset.times[k]);
		}
		return dates;
	}
	
	public ArrayList<Double> fittedValues(Date s, Date e, Cosine wave)
	{
		ArrayList<Double> values = new ArrayList<Double>();
		int i = dateToIndex(s);
		int j = dateToIndex(e);
		for(int k=i; k<j && k<dset.times.length; k++)
		{
			values.add(getValueFromDate(dset.times[k],wave));
		}
		return values;
	}
	
	public ArrayList<Date> outlierDates()
	{
		ArrayList<Date> dates = new ArrayList<Date>();
		for(int i : outliers)
		{
			dates.add(indexToDate(i));
		}
		return dates;
	}
	
	public ArrayList<Double> outlierValues()
	{
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i : outliers)
		{
			values.add(dset.values[i]);
		}
		return values;
	}
	

}