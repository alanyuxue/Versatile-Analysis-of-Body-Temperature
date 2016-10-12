import java.util.*;

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
	
	public ArrayList<Integer> getOutliers(Cosine curve, double thresh)
	{
		int N = dset.N;
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<N; i++)
		{
			double dif = Math.abs(curve.getValue(i*dset.rate)-dset.values[i]);
			if(dif > thresh*curve.getAmplitude())
				list.add(i);
		}
		return list;
	}
	
	public void printOutlierRanges(Cosine curve, double thresh)
	{
		ArrayList<Integer> outliers = getOutliers(curve,thresh);
		int N = dset.N;
		boolean[] isOutlier = new boolean[N];
		for(int i : outliers)
		{
			isOutlier[i] = true;
		}
		int i=0;
		int start = -1;
		while(i < N)
		{
			if(isOutlier[i])
			{
				if(start == -1)
					start = i;
			}
			else
			{
				if(start != -1)
				{
					if(start == i-1)
					{
						System.out.println(start);
					}
					else
					{
						System.out.println(start+"-"+(i-1));
					}
				}
				start = -1;
			}
			i++;
		}
		if(start != -1)
		{
			if(start == i-1)
			{
				System.out.println(start);
			}
			else
			{
				System.out.println(start+"-"+(i-1));
			}
		}
	}
}