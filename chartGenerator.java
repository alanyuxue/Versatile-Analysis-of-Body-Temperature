
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
import java.util.Date;

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

	public chartGenerator(DataSet data)
   {
      super("Temperature Analysis");
      JFreeChart xylineChart = ChartFactory.createTimeSeriesChart(
    	         "Temperature Analysis" ,
    	         "Time" ,
    	         "Temperature" ,
    	         createDataset(data) ,
    	         true , true , false);

      ChartPanel chartPanel = new ChartPanel( xylineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 1280 , 720 ) );

      final XYPlot plot = xylineChart.getXYPlot( );

      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
      renderer.setSeriesPaint( 0 , Color.RED );
      renderer.setSeriesStroke( 0 , new BasicStroke( 4.0f ) );
      DateAxis dateAxis = (DateAxis)plot.getDomainAxis();
      dateAxis.setDateFormatOverride(new SimpleDateFormat("dd/MM/YYYY HH:mm"));
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

   private static XYDataset createDataset(DataSet data){
	     final TimeSeries series = new TimeSeries(data.name);
		   //initialise DataSet attributes
		   double d;
		   int N = data.N;
		   for(int i=0; i<N; i++)
		   {
		     d = data.values[i];
		     series.addOrUpdate(new Minute(data.times[i]),d);
		   }
		   dset = data;
	     final TimeSeriesCollection dataset = new TimeSeriesCollection();
	     dataset.addSeries(series);
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
