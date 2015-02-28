package com.infy.hackathon.na.processor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.infy.hackathon.na.constants.AntDirectory;
 
public class MaventoANTPanel extends JPanel {
	
	private Map<String, String> antResources = new HashMap<String, String>();
	
    public MaventoANTPanel() {
        super(new GridLayout(1, 1));
         
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");
         
        JComponent projectFolderPanel = makeProjectFolderPanel("Specify ANT project folder path:",tabbedPane);
       
        //projectFolderPanel.setPreferredSize(new Dimension(600, 400));
       // JTextComponent jTextField = new JTextField();
        tabbedPane.addTab("Project Folder", icon, projectFolderPanel,
                "Does nothing");
        
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
         
        JComponent panel2 = makeSourceFolderPanel("Enter Source Folder Path",tabbedPane);
        tabbedPane.addTab("Configure Source", icon, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
         
        JComponent panel3 = makeTestFolderPanel("Enter JUnit Test Folder Path", tabbedPane);
        tabbedPane.addTab("Configure Test", icon, panel3,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
         
        JComponent panel4 = makeLibraryPanel(
                "Enter Lib Folder Path", tabbedPane);
        panel4.setPreferredSize(new Dimension(410, 50));
        tabbedPane.addTab("Configure Libraries", icon, panel4,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
         
        //Add the tabbed pane to this panel.
        add(tabbedPane);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
     
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.LEFT);
        filler.setVerticalAlignment(JLabel.TOP);
        JTextField projectFolderPath = new JTextField(20);
        /*projectFolderPath.setBounds(51, 200, 164, 94);
        projectFolderPath.setSize(200, 100);*/
        //projectFolderPath.setPreferredSize(new Dimension(100, 30));
        //panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        panel.add(projectFolderPath);
        return panel;
    }
    
    protected JComponent makeTestFolderPanel(String text, final JTabbedPane tabbedPane) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.LEFT);
        filler.setVerticalAlignment(JLabel.TOP);
        JTextField testFolderPath = new JTextField(20);
       
        panel.add(filler);
        panel.add(testFolderPath);
        
        navigateTabBack(panel,tabbedPane,1,"Back");
        
        navigateTabNext(panel,tabbedPane,3,"Next",AntDirectory.TESTSRC.toString(),testFolderPath);    
        
        return panel;
    }
    
    protected JComponent makeLibraryPanel(String text, final JTabbedPane tabbedPane) {
        final JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.LEFT);
        filler.setVerticalAlignment(JLabel.TOP);
        final JTextField libFolderPath = new JTextField(20);
       
        panel.add(filler);
        panel.add(libFolderPath);
        
        navigateTabBack(panel,tabbedPane,2,"Back");
        
        JButton subButton = new JButton("Submit");
        subButton.setBounds(75, 82, 90, 31);
       // nextButton.
        panel.add(subButton);
        subButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		antResources.put(AntDirectory.LIB.toString(), libFolderPath.getText());
        		System.out.println("antResources: " + antResources);
        		String antResourcesStr = "";
        		for(Map.Entry<String, String> entry : antResources.entrySet()){
        			antResourcesStr = antResourcesStr + entry.getKey() + ": " + entry.getValue() + "\n";
        		}
        		JOptionPane.showMessageDialog(panel, antResourcesStr);
        		Convertor convertor = new Convertor();
        		convertor.convertAntToMaven(antResources);
        		JOptionPane.showMessageDialog(panel, "Maven Project created succesfully!!!");
        	}
        	
        });
       // navigateTab(panel,tabbedPane,3,"Next");       
        
        return panel;
    }
    
    protected JComponent makeSourceFolderPanel(String text, final JTabbedPane tabbedPane) {
        JPanel panel = new JPanel(false);
       
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.LEFT);
        filler.setVerticalAlignment(JLabel.TOP);
        JTextField sourceFolderPath = new JTextField(20);
       
		//String sourceFolder = projectFolderPath.getSelectedText());
		panel.add(filler);
        panel.add(sourceFolderPath);
        
        navigateTabBack(panel,tabbedPane,0,"Back");
        
        navigateTabNext(panel,tabbedPane,2,"Next",AntDirectory.SRC.toString(),sourceFolderPath);       
        
        return panel;
    }
    
    private void navigateTabBack(JPanel panel,final JTabbedPane tabbedPane,final int index, String label){
    	 JButton backButton = new JButton(label);
         backButton.setBounds(75, 82, 90, 31);
        // nextButton.
         panel.add(backButton);
         backButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		System.out.println("antResources: " + antResources);
         		tabbedPane.setSelectedIndex(index);
         	}
         	
         });
    }
    
    private void navigateTabNext(JPanel panel,final JTabbedPane tabbedPane,final int index, String label,
    		final String panelKey, final JTextField folderName){
    	 JButton backButton = new JButton(label);
         backButton.setBounds(75, 82, 90, 31);
        // nextButton.
         panel.add(backButton);
         backButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		antResources.put(panelKey, folderName.getText());
         		System.out.println("folderName.getSelectedText(): " + folderName.getText());
         		System.out.println("antResources: " + antResources);
         		tabbedPane.setSelectedIndex(index);
         	}
         	
         });
    }
    
    private void navigateTab(JPanel panel,final JTabbedPane tabbedPane,final int index, String label
    		){
    	 JButton backButton = new JButton(label);
         backButton.setBounds(75, 82, 90, 31);
        // nextButton.
         panel.add(backButton);
         backButton.addActionListener(new ActionListener() {
         	public void actionPerformed(ActionEvent arg0) {
         		tabbedPane.setSelectedIndex(index);
         	}
         	
         });
    }
    
    protected JComponent makeProjectFolderPanel(String text, final JTabbedPane tabbedPane) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        
        //filler.setHorizontalAlignment(JLabel.LEFT);
       // filler.setVerticalAlignment(JLabel.TOP);
        final JTextField projectFolderPath = new JTextField(20);
       
        panel.add(filler);
        panel.add(projectFolderPath);
        
        final JButton browseButton = new JButton("Browse");
        //btnNewButton.setBounds(258, 26, 105, 31);
        
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try{
                JFileChooser filedilg=new JFileChooser();
                filedilg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                filedilg.showOpenDialog(filedilg);
                String fileName=filedilg.getSelectedFile().getAbsolutePath();
                projectFolderPath.setText(fileName);
                System.out.println("fileName: " + fileName);
                antResources.put(AntDirectory.PROJECT_FOLDER.toString(), fileName);
                
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
          });
        //browseButton.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        
        panel.add(browseButton);
        
        navigateTab(panel,tabbedPane,1,"Next");  
        return panel;
    }
     
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MaventoANTPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TabbedPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Add content to the window.
        frame.add(new MaventoANTPanel(), BorderLayout.CENTER);
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        createAndShowGUI();
            }
        });
    }
}