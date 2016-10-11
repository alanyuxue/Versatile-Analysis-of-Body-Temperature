
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JScrollBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.data.time.*;

public class chartGenerator extends ApplicationFrame
{
	private static DataSet dset;

	public chartGenerator(File file)
   {
      super("Temperature Analysis");
      JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(
    	         "Temperature Analysis" ,
    	         "Time" ,
    	         "Temperature" ,
    	         createDataset(file) ,
    	         true , true , false);

      ChartPanel chartPanel = new ChartPanel( xylineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 1920 , 1080 ) );

      final XYPlot plot = xylineChart.getXYPlot( );

      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
      renderer.setSeriesPaint( 0 , Color.RED );
      renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
      DateAxis dateAxis = (DateAxis)plot.getDomainAxis();
      dateAxis.setDateFormatOverride(new SimpleDateFormat("DD/MM/YYYY HH:mm"));
      dateAxis.setVerticalTickLabels(true);

      plot.setRenderer( renderer );
      plot.setDomainPannable(true);
      this.add(chartPanel);
      this.add(getScrollBar(dateAxis),BorderLayout.SOUTH);
      this.pack();
   }

	private JScrollBar getScrollBar(final DateAxis domainAxis){
	        final double r1 = domainAxis.getLowerBound();
	        final double r2 = domainAxis.getUpperBound();
	        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 400);
	        scrollBar.addAdjustmentListener( new AdjustmentListener() {
	            public void adjustmentValueChanged(AdjustmentEvent e) {
	                double x = e.getValue() *60 *60 * 1000;
	                domainAxis.setRange(r1+x, r2+x);
	            }
	        });
	        return scrollBar;
	    }

   private static XYDataset createDataset(File file){
	     final TimeSeries sheep = new TimeSeries("Sheep");

//	     File file = new File("data.csv");
//	     System.out.println(file.getAbsolutePath());
	     FileInputStream fis = null;
	     BufferedInputStream bis = null;
	     DataInputStream dis = null;
	     try{
	       fis = new FileInputStream(file);
	       bis = new BufferedInputStream(fis);
	       dis = new DataInputStream(bis);
	       dis.readLine();
	       int day;
	       int month;
	       int year;
	       int hour;
	       int minute;
	       String[] parts;
	       String[] dateTime;
	       String dateString;
	       String timeString;
	       String[] date;
	       String[] time;

	       //initialise DataSet attributes
	       int samplingRate;
	       double[] data;
	       double d;
	       int oldHour = 0, newHour = 0 , oldMinute = 0, newMinute = 0;
	       ArrayList<Double> datalist = new ArrayList<Double>();

	       while(dis.available() != 0){
	         String line = dis.readLine();
	         parts = line.split(",");
//	         System.out.println("Parts is "+parts[0]+" "+parts[1]);
	         dateTime= parts[0].split(" ");
//	         System.out.println("Date "+dateTime[0]+" Time "+dateTime[1]);
	         dateString = dateTime[0];
	         timeString = dateTime[1];
	         date = dateString.split("/");
	         time = timeString.split(":");
	         day = Integer.parseInt(date[0]);
	         month = Integer.parseInt(date[1]);
	         year = Integer.parseInt(date[2]);
	         hour = Integer.parseInt(time[0]);
	         minute = Integer.parseInt(time[1]);
//	         System.out.println(day);
//	         System.out.println(month);
//	         System.out.println(year);
//	         System.out.println(hour);
//	         System.out.println(minute);
	         d = Double.parseDouble(parts[1]);
	         sheep.add(new Minute(minute,hour,day,month,year),d);
	         datalist.add(d);

	         //calculate sampling rate
	         if ( (datalist.size() & 1) == 0) {
	        	 oldHour = hour;
	        	 oldMinute = minute;
	         } else {
	        	 newHour = hour;
	        	 newMinute = minute;
	         }
	       }
	       fis.close();
	       bis.close();
	       dis.close();

	       samplingRate = newHour * 60 + newMinute - oldHour * 60 - oldMinute;
	       data = new double[datalist.size()];
	       for (int i = 0; i < datalist.size(); i++) {
	    	   data[i] = datalist.get(i).doubleValue();
	       }
	       dset = new DataSet(samplingRate,datalist.size(),data);
	     }

	     catch(FileNotFoundException e){
	       e.printStackTrace();
	     }
	     catch(IOException e){
	       e.printStackTrace();
	     }
	     final TimeSeriesCollection dataset = new TimeSeriesCollection( );
	     dataset.addSeries(sheep);
	     return dataset;
   }

   public DataSet getDataSet() {
	   return dset;
   }
/*
   public static void main( String[ ] args )
   {
      chartGenerator chart = new chartGenerator("Temperature Analysis", "Temperature Analysis");
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
*/
}
