import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;


public class chartGenerator extends ApplicationFrame {

    private static final String title = "Versatile Analysis of Body Temperature";
    private ChartPanel chartPanel;
    private static DataSet dset;

    public chartGenerator(DataSet data) {
        super("Temperature Analysis");
    	JFrame f = new JFrame(title);
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        chartPanel = createChart(data);
        f.add(chartPanel, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(createZoomOut());
        panel.add(createZoomIn());
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    private ChartPanel createChart(DataSet data) {
        TimeSeriesCollection roiData = createDataset(data);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title, "Date", "Temperature", roiData, true, true, false);
        
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        XYLineAndShapeRenderer renderer =
            (XYLineAndShapeRenderer) plot.getRenderer();
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        return new ChartPanel(chart);
    }
    
    private static TimeSeriesCollection createDataset(DataSet data){
	     final TimeSeries sheep = new TimeSeries("Sheep");
	     
/*---------------------old file reading code-------------------------------------
	     File file = new File("data.csv");
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

	         System.out.println(day);
	         System.out.println(month);
	         System.out.println(year);
	         System.out.println(hour);
	         System.out.println(minute);

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
--------------------------------------------------------------------------------------*/

		 //initialise DataSet attributes
		 double d;
		 ArrayList<Double> datalist = new ArrayList<Double>();
		 int N = data.N;
		 for(int i=0; i<N; i++)
		 {
		   d = data.values[i];
		   sheep.addOrUpdate(new Minute(data.times[i]),d);
		   datalist.add(d);
		 }
		 dset = data;
	     final TimeSeriesCollection dataset = new TimeSeriesCollection( );
	     dataset.addSeries(sheep);
	     return dataset;
  }
    
    private JButton createZoomOut() {
        final JButton auto = new JButton(new AbstractAction("Zoom Out") {
            @Override
            public void actionPerformed(ActionEvent e) {
            	final double r1 = chartPanel.getChart().getXYPlot().getRangeAxis().getLowerBound();
                final double r2 = chartPanel.getChart().getXYPlot().getRangeAxis().getUpperBound();
            	chartPanel.getChart().getXYPlot().getRangeAxis().setLowerBound(r1-5);
            	chartPanel.getChart().getXYPlot().getRangeAxis().setUpperBound(r2+5);
            }
        });
        return auto;
    }
    
    private JButton createZoomIn() {
        final JButton auto = new JButton(new AbstractAction("Zoom In") {
            @Override
            public void actionPerformed(ActionEvent e) {
            	final double r1 = chartPanel.getChart().getXYPlot().getRangeAxis().getLowerBound();
                final double r2 = chartPanel.getChart().getXYPlot().getRangeAxis().getUpperBound();
            	chartPanel.getChart().getXYPlot().getRangeAxis().setLowerBound(r1+5);
            	chartPanel.getChart().getXYPlot().getRangeAxis().setUpperBound(r2-5);
            }
        });
        return auto;
    }
    
    public DataSet getDataSet() {
 	   return dset;
    }
/*    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
               chartGenerator cpd = new chartGenerator();
            }
        });
    }
*/
}