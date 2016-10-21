package cits3200;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

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
    public Date start, end;
    double outlier = 2.0;
    ArrayList<XYAnnotation> outliers = new ArrayList<XYAnnotation>();
    
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
        		"Temperature Analysis #" + openChartCount, 
        		"Date", 
        		"Temperature", 
        		roiData, 
        		true, true, false);
        
        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        XYLineAndShapeRenderer renderer =
            (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0,Color.black);
        renderer.setSeriesPaint(1,Color.blue);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        return new ChartPanel(chart);
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

    public void addOutliers(ArrayList<Date> dates, ArrayList<Double> values){
    	if(outliers.size()!=0){
    		int size = outliers.size();
    		for(int i =0; i <size;i++){
    			chartPanel.getChart().getXYPlot().removeAnnotation(outliers.get(i));
    		}
    		
    	}
    	outliers = new ArrayList<XYAnnotation>();
    	Ellipse2D e = new Ellipse2D.Double(-50.0, -50.0, 100.0, 100.0);
    	int size = dates.size();
    	for(int i =0; i< size;i++){
            final CircleDrawer cd = new CircleDrawer(Color.red, new BasicStroke(1.0f), null);
            double millis = dates.get(i).getTime();
            double value = values.get(i);
            final XYAnnotation outliersAnnotation = new XYDrawableAnnotation(millis, value, 11, 11, cd);
            outliers.add(outliersAnnotation);
    		chartPanel.getChart().getXYPlot().addAnnotation(outliersAnnotation);
    	}
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
            	chartPanel.getChart().getXYPlot().getRangeAxis().setLowerBound(r1-1);
            	chartPanel.getChart().getXYPlot().getRangeAxis().setUpperBound(r2+1);
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
            	chartPanel.getChart().getXYPlot().getRangeAxis().setLowerBound(r1+1);
            	chartPanel.getChart().getXYPlot().getRangeAxis().setUpperBound(r2-1);
            }
        });
        return auto;
    }
    
	private JFormattedTextField lowerBound() {
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	JFormattedTextField lowerBound = new JFormattedTextField(df);
    	lowerBound.setColumns(16);
    	lowerBound.setMaximumSize(new Dimension(300,30));
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
        
        lowerBound.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            lowerBound.setText(df.format(start));
	        }

			@Override
			public void focusLost(FocusEvent e) {
				lowerBound.setText(df.format(start));
			}
	    });
        return lowerBound;
    }
    
	private JFormattedTextField upperBound() {
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	JFormattedTextField upperBound = new JFormattedTextField(df);
    	upperBound.setColumns(10);
    	upperBound.setMaximumSize(new Dimension(300,30));
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
    	
    	upperBound.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            upperBound.setText(df.format(end));
	        }

			@Override
			public void focusLost(FocusEvent e) {
				upperBound.setText(df.format(end));
			}
	    });
        return upperBound;
    }
    
	private JTextField outlierTextField() {
		JTextField outlierTextField = new JTextField();
		outlierTextField.setMaximumSize(new Dimension(300,30));
		outlierTextField.setText("2.0");
		
		outlierTextField.addKeyListener(new KeyAdapter() {
    	    public void keyTyped(KeyEvent e) {
      	      char c = e.getKeyChar();
      	      if (!((c >= '0') && (c <= '9') ||
      	    		  (c == KeyEvent.VK_BACK_SPACE) ||
      	    	         (c == KeyEvent.VK_DELETE) || 
      	    	         (c == KeyEvent.VK_ENTER) || 
      	    	         (c == KeyEvent.VK_PERIOD)))
      	      {
      	        JOptionPane.showMessageDialog(null, "Please Enter Valid");
      	        e.consume();
      	      }
      	    }
      	  });
		
		outlierTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					outlier = Double.parseDouble(outlierTextField.getText());
					
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "Please enter a double number");
				}
			}
		});
		
		outlierTextField.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            outlierTextField.setText(Double.toString(outlier));
	        }

			@Override
			public void focusLost(FocusEvent e) {
				outlierTextField.setText(Double.toString(outlier));
			}
	    });
		
		return outlierTextField;
	}
	
    private JButton analyse() {
    	JButton analyse = new JButton(new AbstractAction("Analyse") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DecimalFormat df = new DecimalFormat(".##");
				a = new Analyser(dset);
				double p = a.getPeriod(a.dateToIndex(start),a.dateToIndex(end));
		    	wave = a.doCosinor(p,a.dateToIndex(start),a.dateToIndex(end));
				double MSR = a.getMSR(wave);
				result.startDate.setText("Start: "+start.toString());
				result.endDate.setText("End: "+end.toString());
				result.out.setText("Outlier Tolerance: "+df.format(outlier));
				result.rate.setText("Rate: "+ dset.rate+ " minutes between each sample");
		    	result.period.setText("Period: "+ df.format(p)+ " minutes ("+df.format(p/60)+" hours)");
		    	result.mesor.setText("MESOR: "+df.format(wave.getMESOR()));
		    	result.amplitude.setText("Amplitude: "+df.format(wave.getAmplitude()));
		    	result.acrophase.setText("Acrophase: "+df.format(wave.getAcrophase())+" minutes("+df.format(wave.getAcrophase()/60)+" hours)");
		    	result.msr1.setText("Mean Square Residual: "+df.format(MSR)+"    ");
		    	result.msr2.setText("        ("+df.format(100*MSR/wave.getAmplitude())+"% of amplitude)");
		    	addData(a.fittedDates(start,end),a.fittedValues(start,end,wave));
		    	a.getOutliers(a.dateToIndex(start),a.dateToIndex(end), wave, outlier);
		    	ArrayList<Date> dates = a.outlierDates();
				ArrayList<Double> values = a.outlierValues();
				addOutliers(dates,values);
				
			}
		});
    	return analyse;
    }
    
    private void removeOutliers(){
    	int size = outliers.size();
    	for(int i = 0;i<size;i++){
    		chartPanel.getChart().getXYPlot().removeAnnotation(outliers.get(i));
    	}
			
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
	
	
	
    private class ResultPanel extends JPanel{
    	
    	JLabel startDate = new JLabel();
    	JLabel endDate = new JLabel();
    	JLabel out = new JLabel();
    	JLabel rate = new JLabel();
    	JLabel period = new JLabel();
    	JLabel mesor = new JLabel();
    	JLabel amplitude = new JLabel();
    	JLabel acrophase = new JLabel();
		JLabel msr1 = new JLabel();
		JLabel msr2 = new JLabel();
		JFormattedTextField lower = lowerBound();
        JFormattedTextField upper = upperBound();
        JTextField outlierText = outlierTextField();
		
    	private ResultPanel() {
        	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        	
        	JLabel lowerLabel = new JLabel("Start Date and Time (dd/mm/yyyy hh:mm):");
            JLabel upperLabel = new JLabel("End Date (dd/mm/yyyy):");
            JLabel outlierLabel = new JLabel("Outlier Tolerance:");
            
            add(lowerLabel);
            add(lower);
            add(upperLabel);
            add(upper);
            add(outlierLabel);
            add(outlierText);
            add(analyse());
            add(produceReport());
            add(reset());
            
        	JLabel title = new JLabel("Results:");
        	add(title);
        	add(startDate);
            add(endDate);
            add(out);
        	add(rate);
	    	add(period);
	    	add(mesor);
	    	add(amplitude);
	    	add(acrophase);
	    	add(msr1);
	    	add(msr2);
    	}
    	
    	
    	private JButton reset() {
    		JButton reset = new JButton(new AbstractAction("Reset") {
    			
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				start = dset.startDate;
    				end = dset.endDate;
    				outlier = 2.0;
    				lower.setText("");
    				upper.setText("");
    				outlierText.setText(Double.toString(outlier));
    				chartPanel.getChart().getXYPlot().getDomainAxis().setRange((double) start.getTime(),(double) end.getTime());
    				roiData.removeSeries(analysis);
    				removeOutliers();
    			}
    		});
    		return reset;
    	}
    }
}