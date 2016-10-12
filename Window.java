import javax.swing.JInternalFrame;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.ui.RefineryUtilities;

import javax.swing.JMenuBar;
import javax.swing.JFrame;
import java.awt.event.*;
import java.io.File;
import java.awt.*;

public class Window extends JFrame implements ActionListener
{
    JDesktopPane desktop;
    int width;
	  int height;
    boolean fullScreen;
	  int tabs;

    public Window() {
        super("Window");
        width = 1280;
		height = 720;
		fullScreen = true;
		tabs = 0;
		initUI();
	}

    public void initUI() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (fullScreen == true) {
        	//this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        	//this.setUndecorated(true);
        	setBounds(0, 0,screenSize.width,screenSize.height);
        }
        else {
        	//Place the window in the middle of the screen
        	setBounds((screenSize.width-width)/2, (screenSize.height-height)/2,width,height);
        }
        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);
        setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    protected JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

		//--------------------File menu--------------------

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		//Open
		//ImageIcon openIcon = new ImageIcon("open.png");
		JMenuItem openMenuItem = new JMenuItem("Open...");
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setToolTipText("Open data files");
		openMenuItem.addActionListener(new OpenFileAction());
		fileMenu.add(openMenuItem);

		//Open recent
		JMenu openRecentMenu = new JMenu("Open Recent");
		fileMenu.add(openRecentMenu);
		fileMenu.addSeparator();

		//Print
		//ImageIcon printIcon = new ImageIcon("print.png");
		JMenuItem printMenuItem = new JMenuItem("Print");
		printMenuItem.setMnemonic(KeyEvent.VK_P);
		printMenuItem.setToolTipText("Print results");
		printMenuItem.addActionListener((ActionEvent event) -> {

		});
		fileMenu.add(printMenuItem);

		//Export As
		JMenu exportAsMenu = new JMenu("Export As");
		fileMenu.add(exportAsMenu);
		fileMenu.addSeparator();

		//Exit
		//ImageIcon exitIcon = new ImageIcon("exit.png");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.setToolTipText("Exit application");
		exitMenuItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		fileMenu.add(exitMenuItem);

		//Add file menu to menu bar
		menuBar.add(fileMenu);

		//---------------Dataset Analysis Menu-------------

		JMenu datasetAnalysisMenu = new JMenu("Dataset Analysis");

		//Run Curve Analysis
		//ImageIcon runAnalysisIcon = new ImageIcon("run.png");
		JMenuItem runAnalysisMenuItem = new JMenuItem("Run Curve Analysis");
		runAnalysisMenuItem.setMnemonic(KeyEvent.VK_R);
		runAnalysisMenuItem.setToolTipText("Run Curve Analysis");
		runAnalysisMenuItem.addActionListener((ActionEvent event) -> {

		});
		datasetAnalysisMenu.add(runAnalysisMenuItem);

		//Periodogram
		JCheckBoxMenuItem periodogram = new JCheckBoxMenuItem("Periodogram");
		periodogram.setMnemonic(KeyEvent.VK_P);
		periodogram.setDisplayedMnemonicIndex(0);
		periodogram.setSelected(true);

		periodogram.addItemListener((ItemEvent e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

			} else {

			}
		});
		datasetAnalysisMenu.add(periodogram);

		//Residuals
		JCheckBoxMenuItem residuals = new JCheckBoxMenuItem("Residuals");
		residuals.setMnemonic(KeyEvent.VK_R);
		residuals.setDisplayedMnemonicIndex(0);
		residuals.setSelected(true);

		periodogram.addItemListener((ItemEvent e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

			} else {

			}
		});
		datasetAnalysisMenu.add(residuals);

		//Curve Fitting
		JCheckBoxMenuItem curveFitting = new JCheckBoxMenuItem("Curve Fitting");
		curveFitting.setMnemonic(KeyEvent.VK_F);
		curveFitting.setDisplayedMnemonicIndex(6);
		curveFitting.setSelected(true);

		curveFitting.addItemListener((ItemEvent e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

			} else {

			}
		});
		datasetAnalysisMenu.add(curveFitting);

		//Raw Data
		JCheckBoxMenuItem rawData = new JCheckBoxMenuItem("Raw Data");
		rawData.setMnemonic(KeyEvent.VK_D);
		rawData.setDisplayedMnemonicIndex(4);
		rawData.setSelected(true);

		rawData.addItemListener((ItemEvent e) -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {

			} else {

			}
		});
		datasetAnalysisMenu.add(rawData);

		// Add data analysis menu to menu bar
		menuBar.add(datasetAnalysisMenu);

		//--------------------Help Menu--------------------

		JMenu helpMenu = new JMenu("Help");

		//Documentation
		ImageIcon docIcon = new ImageIcon("documentation.png");
		JMenuItem docMenuItem = new JMenuItem("Documentation",docIcon);
		docMenuItem.setMnemonic(KeyEvent.VK_D);
		docMenuItem.setToolTipText("Open documentation");
		docMenuItem.addActionListener((ActionEvent event) -> {

		});
		helpMenu.add(docMenuItem);

		//Add help menu to the RIGHT side of menu bar
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(helpMenu);

        return menuBar;
    }

	private class OpenFileAction extends AbstractAction {

		JDesktopPane panel = (JDesktopPane) getContentPane();

		@Override
		public void actionPerformed (ActionEvent e) {
			JFileChooser fdia = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
			fdia.addChoosableFileFilter(filter);

			int ret = fdia.showDialog(panel, "Open file");
			if (ret == JFileChooser.APPROVE_OPTION) {
//				System.out.println("file opened");
				File file =fdia.getSelectedFile();
				processFile(file);
			}
//			else System.out.println("file cannot be opened.");
		}
	}

    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
 /*
        if ("new".equals(e.getActionCommand())) { //new
            createFrame();
        } else { //quit
            quit();
        }
 */
    }

    public void processFile(File file){
    	
    	//analyse data
		System.out.println("Reading file");
		DataSet dset = new DataSet(file);
		Analyser a = new Analyser(dset);
		System.out.println("Performing periodogram");
		double period = a.getPeriod();
		System.out.println("Period: "+period+" minutes");
		System.out.println("Performing Cosinor");
		Cosine wave = a.doCosinor(period);
		System.out.println("MESOR: "+wave.getMESOR());
		System.out.println("Amplitude: "+wave.getAmplitude());
		System.out.println("Acrophase: "+wave.getAcrophase()+" minutes");
		double MSR = a.getMSR(wave);
		System.out.println("Mean Square Residual: "+MSR+"("+(100*MSR/wave.getAmplitude())+"% of amplitude)");
		System.out.println();

		//create chart
		chartGenerator chart = new chartGenerator(dset);
        JInternalFrame internalFrame = new JInternalFrame();
        internalFrame.setContentPane(chart.getContentPane());
        internalFrame.pack();
        internalFrame.setVisible(true); //necessary as of 1.3
        desktop.add(internalFrame);
        RefineryUtilities.centerFrameOnScreen( chart );
	}

    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        Window w = new Window();
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        w.setVisible(true);
    }

    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
