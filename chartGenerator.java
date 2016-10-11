package cits3200;
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
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;


public class chartGenerator {

    private static final String title = "Versatile Analysis of Body Temperature";
    private ChartPanel chartPanel = createChart();

    public chartGenerator() {
        
    	JFrame f = new JFrame(title);
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(createZoomOut());
        panel.add(createZoomIn());
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    
    private ChartPanel createChart() {
        TimeSeriesCollection roiData = createDataset();
        
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
    
    private static TimeSeriesCollection createDataset(){
	     final TimeSeries sheep = new TimeSeries("Sheep");

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

	       while(dis.available() != 0){
	         String line = dis.readLine();
	         parts = line.split(",");
	         System.out.println("Parts is "+parts[0]+" "+parts[1]);
	         dateTime= parts[0].split(" ");
	         System.out.println("Date "+dateTime[0]+" Time "+dateTime[1]);
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
	         sheep.add(new Minute(minute,hour,day,month,year),Double.parseDouble(parts[1]));
	       }
	       fis.close();
	       bis.close();
	       dis.close();
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
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
               chartGenerator cpd = new chartGenerator();
            }
        });
    }
}