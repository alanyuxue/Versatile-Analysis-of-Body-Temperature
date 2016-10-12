import java.util.*;

class Analyser
{
	public DataSet dset;
	
	public Analyser(DataSet ds)
	{
		dset = ds;
	}
	
	public double getPeriod()
	{
		int N = dset.N;
		double[] reals = Arrays.copyOf(dset.data,N);
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

		return (N/maxid)*dset.rate;
	}
	
	public Cosine doCosinor(double period)
	{
		int N = dset.N;
		double[] times = new double[N];
		for(int i=0; i<N; i++)
			times[i] = dset.rate*i;
		return Cosinor.solve(times,dset.data,period);
	}
}