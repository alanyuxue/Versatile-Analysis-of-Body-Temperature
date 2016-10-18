//This class represents a cosine curve of the form Y(t) = M + Acos(2pi*t/tau+phi)
package cits3200;
public class Cosine
{
	public double M;
	public double A;
	public double tau;
	public double phi;
	
	public Cosine(double MArg, double AArg, double tauArg, double phiArg)
	{
		M = MArg;
		A = AArg;
		tau = tauArg;
		phi = phiArg;
	}
	public double getValue(double t) //Evaluate Y(t): the value of the function for a given value of t
	{
		return M+A*Math.cos(2*Math.PI*t/tau+phi);
	}
	public double getPeriod()
	{
		return tau;
	}
	public double getMESOR()
	{
		return M;
	}
	public double getAmplitude()
	{
		return A;
	}
	public double getAcrophase()
	{
		double aphase = -phi*tau/(2*Math.PI);
		while(aphase < 0)
			aphase += tau;
		return aphase;
	}
	public String toString()
	{
			return "Y(t) = "+M+" + "+A+"*cos(2pi*t/"+tau+" + "+phi+")";
	}
}