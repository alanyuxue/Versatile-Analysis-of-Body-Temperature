import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
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

    public Window() {
        super("Versatile Analysis of Body Temperature");
        width = 1280;
		height = 720;
		fullScreen = true;
		initUI();
	}

    public void initUI() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (fullScreen == true) {
        	setBounds(0, 0,screenSize.width,screenSize.height);
        } else {
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
		JMenuItem openMenuItem = new JMenuItem("Open...");
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setToolTipText("Open data files");
		openMenuItem.addActionListener(new OpenFileAction());
		fileMenu.add(openMenuItem);

		//Exit
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.setToolTipText("Exit application");
		exitMenuItem.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		fileMenu.add(exitMenuItem);

		//Add file menu to menu bar
		menuBar.add(fileMenu);

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
				File file =fdia.getSelectedFile();
				processFile(file);
			}
		}
	}

    public void actionPerformed(ActionEvent e) {
    }

    public void processFile(File file){

    	//process data
		try
		{
			DataSet dset = new DataSet(file);
			//create chart
			chartGenerator chart = new chartGenerator(dset);

			chart.setVisible(true);
	        desktop.add(chart);

	        //always put new chart on top of others
	        try {
	            chart.setSelected(true);
	        } catch (java.beans.PropertyVetoException e) {}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Failed to read. File may not be formatted correctly");
		}

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
