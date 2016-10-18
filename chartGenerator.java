import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.crypto.Data;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;


public class chartGenerator extends JInternalFrame {

    public ChartPanel chartPanel;
    private TimeSeriesCollection roiData = new TimeSeriesCollection( );
    private TimeSeries analysis = new TimeSeries("Analysis");
    private static DataSet dset;
    private static Analyser a=null;
    private static Cosine wave;
    static int openChartCount = 0;
    static final int xOffset = 30, yOffset = 30;
    ResultPanel result = new ResultPanel();
    Date start, end;
    double outlier = 2.0;
    
    public chartGenerator(DataSet ds) {
    	super("Temperature Analysis #" + (++openChartCount), 
    			true, 	//resizable
    			true,	//closable
    			true,	//maximisable
    			true);	//iconifiable
    	dset = ds;
    	start = dset.startDate;
    	end = dset.endDate;

    	chartPanel = createChart(dset);
    	chartPanel.getChart().getXYPlot().setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
    	setLayout(new BorderLayout(0, 5));
        add(chartPanel, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(createZoomOut());
        panel.add(createZoomIn());
        
        add(panel, BorderLayout.SOUTH);
        add(result,BorderLayout.EAST);
        pack();
        setVisible(true);
        setLocation(xOffset*openChartCount,yOffset*openChartCount);
    }
    
    
    
    private ChartPanel createChart(DataSet dset) {
        roiData = createDataset(dset);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
        		"Temperature Analysis #" + openChartCount, "Date", "Temperature", roiData, true, true, false);
        
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        XYLineAndShapeRenderer renderer =
            (XYLineAndShapeRenderer) plot.getRenderer();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        return new ChartPanel(chart);
    }
    
    class MenuActionListener implements ActionListener {
  	  public void actionPerformed(ActionEvent e) {
  	    System.out.println("Selected: " + e.getActionCommand());
  	  }
  	}
    private void addData(ArrayList<Date> dates ,ArrayList<Double> values){
    	if (roiData.indexOf(analysis) != -1){
    		roiData.removeSeries(analysis);
    	}
    	analysis = new TimeSeries("Analysis");
    	int length = dates.size();
    	for(int i = 0;i<length;i++){
    		analysis.addOrUpdate(new Minute(dates.get(i)),values.get(i));
    	}
    	
    	roiData.addSeries(analysis);
    }
    
    private static TimeSeriesCollection createDataset(DataSet dset){
	     final TimeSeries series = new TimeSeries(dset.name);
		 double d;
		 int N = dset.N;
		 for(int i=0; i<N; i++)
		 {
		   d = dset.values[i];
		   series.addOrUpdate(new Minute(dset.times[i]),d);
		 }
	     final TimeSeriesCollection dataset = new TimeSeriesCollection( );
	     dataset.addSeries(series);
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
    
	private JFormattedTextField lowerBound() {
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	JFormattedTextField lowerBound = new JFormattedTextField(df);
    	lowerBound.setColumns(16);
    	lowerBound.setMaximumSize(new Dimension(300,30));
//    	lowerBound.setText(df.format(start));
    	lowerBound.addKeyListener(new KeyAdapter() {
    	    public void keyTyped(KeyEvent e) {
    	      char c = e.getKeyChar();
    	      if (!((c >= '0') && (c <= '9') ||
    	         (c == KeyEvent.VK_BACK_SPACE) ||
    	         (c == KeyEvent.VK_DELETE) || 
    	         (c == KeyEvent.VK_ENTER) || 
    	         (c == KeyEvent.VK_SLASH) || 
    	         (c == KeyEvent.VK_SPACE) || 
    	         (c == ':')))        
    	      {
    	        JOptionPane.showMessageDialog(null, "Please Enter Valid");
    	        e.consume();
    	      }
    	    }
    	  });
    	
        lowerBound.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
					start = df.parse(lowerBound.getText());
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
            	if(start.after(dset.startDate) && start.before(dset.endDate)){
            		if (start.before(end)) {
            			chartPanel.getChart().getXYPlot().getDomainAxis().setRange((double) start.getTime(),(double) end.getTime());
            			//dset.startDate = start;
            		}
            		else JOptionPane.showMessageDialog(null, "Lower Bound is Higher than Upper Bound");
				}
            	else JOptionPane.showMessageDialog(null, "Lower Bound is outside date range");
            }
        });
        return lowerBound;
    }
    
	private JFormattedTextField upperBound() {
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	JFormattedTextField upperBound = new JFormattedTextField(df);
    	upperBound.setColumns(10);
    	upperBound.setMaximumSize(new Dimension(300,30));
//    	upperBound.setText(df.format(end));
    	upperBound.addKeyListener(new KeyAdapter() {
    	    public void keyTyped(KeyEvent e) {
    	      char c = e.getKeyChar();
    	      if (!((c >= '0') && (c <= '9') ||
    	    		  (c == KeyEvent.VK_BACK_SPACE) ||
    	    	         (c == KeyEvent.VK_DELETE) || 
    	    	         (c == KeyEvent.VK_ENTER) || 
    	    	         (c == KeyEvent.VK_SLASH)))
    	      {
    	        JOptionPane.showMessageDialog(null, "Please Enter Valid");
    	        e.consume();
    	      }
    	    }
    	  });
    	
    	upperBound.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {

            	try {
					end = df.parse(upperBound.getText());
					
				} catch (ParseException e1) {
					e1.printStackTrace();
				}            	
            	if(end.after(dset.startDate) && end.before(dset.endDate)){
            		if (end.after(start)) {
            			chartPanel.getChart().getXYPlot().getDomainAxis().setRange((double) start.getTime(),(double) end.getTime());
            			//dset.endDate = end;
            		}
            		else JOptionPane.showMessageDialog(null, "Upper Bound is Lower than Lower Bound");
				}
            	else JOptionPane.showMessageDialog(null, "Upper Bound is outside date range");
            }
        });
        return upperBound;
    }
    
	private JTextField outlierTextField() {
		JTextField outlierTextField = new JTextField();
		outlierTextField.setMaximumSize(new Dimension(300,30));
		outlierTextField.setText("2.0");
		try {
			outlier = Double.parseDouble(outlierTextField.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please enter a double number");
		}
		
		outlierTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		return outlierTextField;
	}
	
    private JButton analyse() {
    	JButton analyse = new JButton(new AbstractAction("Analyse") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				a = new Analyser(dset);
				double p = a.getPeriod(a.dateToIndex(start),a.dateToIndex(end));
		    	wave = a.doCosinor(p,a.dateToIndex(start),a.dateToIndex(end));
				double MSR = a.getMSR(wave);
				result.rate.setText("Rate: "+ dset.rate+ " minutes between each sample");
		    	result.period.setText("Period: "+ p+ " minutes");
		    	result.mesor.setText("MESOR: "+wave.getMESOR());
		    	result.amplitude.setText("Amplitude: "+wave.getAmplitude());
		    	result.acrophase.setText("Acrophase: "+wave.getAcrophase()+" minutes");
		    	result.msr1.setText("Mean Square Residual: "+MSR+"    ");
		    	result.msr2.setText("        ("+(100*MSR/wave.getAmplitude())+"% of amplitude)");
		    	addData(a.fittedDates(start,end),a.fittedValues(start,end,wave));
		    	a.getOutliers(a.dateToIndex(start),a.dateToIndex(end), wave, outlier);
		    	ArrayList<Date> dates = a.outlierDates();
				ArrayList<Double> values = a.outlierValues();
				addOutliers(dates,values);
			}
		});
    	return analyse;
    }
    
    
	private JButton produceReport() {
		JButton produceReport = new JButton(new AbstractAction("Produce Report") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(a!=null)
				{
					String str = a.createReport(start,end,wave);
					if(str.equals(""))
						JOptionPane.showMessageDialog(null, "Failed to create report");
					else
						JOptionPane.showMessageDialog(null,"Report created at "+str);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Run analysis before creating report");
				}
				
			}
		});
		
		return produceReport;
	}
    public void addOutliers(ArrayList<Date> dates, ArrayList<Double> values){
    	TimeSeries outliers = new TimeSeries("Outliers");
    	int size = dates.size();
    	for(int i =0; i< size;i++){
    		outliers.addOrUpdate(new Minute(dates.get(i)),values.get(i));
    	}
    	roiData.addSeries(outliers);
    }
	
    private class ResultPanel extends JPanel{
    	
    	JLabel rate = new JLabel();
    	JLabel period = new JLabel();
    	JLabel mesor = new JLabel();
    	JLabel amplitude = new JLabel();
    	JLabel acrophase = new JLabel();
		JLabel msr1 = new JLabel();
		JLabel msr2 = new JLabel();
		
    	private ResultPanel() {
        	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        	
        	JLabel lowerLabel = new JLabel("Start Date and Time (dd/mm/yyyy hh:mm):");
            JLabel upperLabel = new JLabel("End Date (dd/mm/yyyy):");
            JLabel outlierLabel = new JLabel("Outlier Sensitivity:");
            add(lowerLabel);
            add(lowerBound());
            add(upperLabel);
            add(upperBound());
            add(outlierLabel);
            add(outlierTextField());
            add(analyse());
            add(produceReport());
            
        	JLabel title = new JLabel("Results:");
        	add(title);
        	add(rate);
	    	add(period);
	    	add(mesor);
	    	add(amplitude);
	    	add(acrophase);
	    	add(msr1);
	    	add(msr2);
    	}
    }
}