import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

class DataSet
{
	public int rate; //Sampling rate in minutes
	public int N; //number of data points
	public double[] data;

	public DataSet(int r, int n, double[] d)
	{
		rate = r;
		N = n;
		data = d;
	}
}
