package cits3200;

public class Cosinor
{
	public static Cosine solve(double[] times, double[] values, double period)
	{
		int N = times.length;
		double W = 2*Math.PI/(period);
		double Ysum = 0;
		double Ycos = 0;
		double Ysin = 0;
		double SUMcos = 0;
		double SUMsin = 0;
		double SUMcoscos = 0;
		double SUMcossin = 0;
		double SUMsinsin = 0;
		//Sum values
		for(int i=0; i<N; i++)
		{
				Ysum += values[i];
				Ycos += values[i]*Math.cos(W*times[i]);
				Ysin += values[i]*Math.sin(W*times[i]);
				SUMcos += Math.cos(W*times[i]);
				SUMsin += Math.sin(W*times[i]);
				SUMcoscos += Math.cos(W*times[i])*Math.cos(W*times[i]);
				SUMcossin += Math.cos(W*times[i])*Math.sin(W*times[i]);
				SUMsinsin += Math.sin(W*times[i])*Math.sin(W*times[i]);
		}
		/*Fill matrix for solving
		| N          SUMcos     SUMsin      |    |M         |     |Ysum|
		| SUMcos SUMcoscos SUMcossin| x |beta     | =  |Ycos  |
		| SUMsin  SUMcossin SUMsinsin |    |gamma|     |Ysin  | */
		
		double[][] matrix = new double[3][3];
		matrix[0][0] = N;
		matrix[0][1] = SUMcos;
		matrix[0][2] = SUMsin;
		matrix[1][0] = SUMcos;
		matrix[1][1] = SUMcoscos;
		matrix[1][2] = SUMcossin;
		matrix[2][0] = SUMsin;
		matrix[2][1] = SUMcossin;
		matrix[2][2] = SUMsinsin;
		double[] Ys = new double[3];
		Ys[0] = Ysum;
		Ys[1] = Ycos;
		Ys[2] = Ysin;
		
		//Solve the system
		double[] Xs; //Xs[0] = M, Xs[1] = Beta, Xs[2] = Gamma
		try
		{
			Xs = GaussElim.solve(matrix,Ys);
		}
		catch (Exception e)
		{
			return null;
		}
		
		//Convert to useful values
		double MESOR = Xs[0];
		double Amplitude = Math.sqrt(Xs[1]*Xs[1]+Xs[2]*Xs[2]); //A = sqrt(beta^2+gamma^2)
		double Acrophase = Math.atan2(-Xs[2],Xs[1]);	//Phi = arctan(-gamma/beta)+some integer value of pi
		return new Cosine(MESOR, Amplitude, period, Acrophase);
	}
	
	public static double closeness(double[] times, double[] values, Cosine wave)
	{
		int N = times.length;
		double sum = 0;
		for(int i=0; i<N; i++)
		{
			sum += Math.abs(values[i]-wave.getValue(times[i]));
		}
		return sum/N;
	}
}