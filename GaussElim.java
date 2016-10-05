public class GaussElim
{
	public static final double EP = 1e-10;
	
	public static double[] solve(double[][] A, double[] b) throws Exception
	{
		int N = b.length;
		
		//Pivotting - Reduce to Row
		for(int p=0; p<N; p++)
		{
			//Find pivot row - largest absolute value in column
			int max = p;
			for(int i=p+1; i<N; i++)
			{
				if(Math.abs(A[i][p]) > Math.abs(A[max][p]))
					max = i;
			}
			//Swap rows
			double[] tempRow = A[p];
			A[p] = A[max];
			A[max] = tempRow;
			double tempVal  = b[p];
			b[p] = b[max];
			b[max] = tempVal;
			
			//Check if singular (uninvertible)
			if(Math.abs(A[p][p]) <= EP)
				throw new Exception("Singular Matrix - No Solution");
			
			//Reduce rows
			for(int i=p+1; i<N; i++)
			{
				double c = A[i][p] / A[p][p];
				b[i] -= c*b[p];
				for(int j=p; j<N; j++)
					A[i][j] -= c*A[p][j];
			}
			
		}
		
		//Back Substitution
		double[] result = new double[N];
		for(int i=N-1; i>= 0; i--)
		{
			double sum = 0.0;
			for(int j=i+1; j<N; j++)
				sum+= A[i][j]*result[j];
			result[i] = (b[i]-sum)/A[i][i];
		}
		return result;
	}
}