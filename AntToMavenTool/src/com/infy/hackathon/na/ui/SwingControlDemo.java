package com.infy.hackathon.na.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;



public class SwingControlDemo extends JFrame {

   /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JFrame mainFrame;
   private JPanel controlPanel;
   private String filename;
   private String fname;
   private JProgressBar progressBar;
   private Task task;
   private JButton startButton;
   private JTextArea outputTextArea;
   private JTextField textField;
   private JLabel lblFilename;
   private JButton btnNewButton;
   private JButton btnCancel;
   private JLabel sendinglabel;
   private JLabel starttime;
   private JLabel endtime;
   private JLabel sendinglabel1;


   public SwingControlDemo(){
      prepareGUI();
   }

   public static void main(String[] args){
      SwingControlDemo  swingControlDemo = new SwingControlDemo();      
      swingControlDemo.showProgressBarDemo();
   }

   private void prepareGUI(){
      mainFrame = new JFrame("Large File Upload");
      mainFrame.setSize(492,400);
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });
      mainFrame.getContentPane().setLayout(null);

      controlPanel = new JPanel();
      controlPanel.setBounds(0, 0, 491, 355);
      mainFrame.getContentPane().add(controlPanel);
      controlPanel.setLayout(null);
      mainFrame.setVisible(true);  
   }



   private void showProgressBarDemo(){
      controlPanel.setLayout(null);

      textField = new JTextField();
      textField.setBounds(125, 26, 105, 31);
      controlPanel.add(textField);
      textField.setColumns(10);

        sendinglabel=new JLabel("FIle is Sending...");
        sendinglabel.setBounds(10, 128, 168, 30);
        sendinglabel.setVisible(false);
        sendinglabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        controlPanel.add(sendinglabel);

        sendinglabel1=new JLabel("FIle is Sending...");
        sendinglabel1.setBounds(51, 200, 250, 30);
        sendinglabel1.setVisible(false);
        sendinglabel1.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        controlPanel.add(sendinglabel1);


        endtime = new JLabel("");
        endtime.setBounds(204, 200, 208, 33);
        endtime.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        controlPanel.add(endtime);


        starttime = new JLabel("");
        starttime.setBounds(273, 141, 208, 33);
        starttime.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        controlPanel.add(starttime);

      ProgressMonitor pc=new ProgressMonitor(SwingControlDemo.this, "Running My Program","", 0,10);


                  progressBar = new JProgressBar(0, 100);
                  progressBar.setBounds(62, 158, 146, 31);
                  progressBar.setValue(0);
                  progressBar.setStringPainted(true);
                  controlPanel.add(progressBar);


                  startButton = new JButton("Send");
                  startButton.setBounds(75, 82, 90, 31);
                  startButton.addActionListener(new ActionListener() {
                  private int i;

               
                  public void actionPerformed(ActionEvent e) {

                    task = new Task();                
                    task.start();

                }});

                        controlPanel.add(startButton);

                              outputTextArea = new JTextArea("",5,20);
                              outputTextArea.setVisible(false);
                              outputTextArea.setBounds(51, 200, 164, 94);
                              controlPanel.add(outputTextArea);

                              lblFilename = new JLabel("FileName");
                              lblFilename.setVisible(true);
                              lblFilename.setBounds(10, 26, 105, 31);
                              lblFilename.setFont(new Font("Times New Roman", Font.PLAIN, 14));
                              controlPanel.add(lblFilename);


                              btnNewButton = new JButton("Browse");
                              btnNewButton.setBounds(258, 26, 105, 31);
                              btnNewButton.addActionListener(new ActionListener() {




                                public void actionPerformed(ActionEvent arg0) {
                                	try{
                                    JFileChooser filedilg=new JFileChooser();
                                    filedilg.showOpenDialog(filedilg);
                                    filename=filedilg.getSelectedFile().getAbsolutePath();
                                    textField.setText(filename);

                                    File file1=new File(filename);
                                    fname = file1.getName();           
                                    System.out.println("THE FILE NAME IS "+fname);
                                	}
                                	catch(Exception e){
                                		e.printStackTrace();
                                	}
                                }
                              });
                              btnNewButton.setFont(new Font("Times New Roman", Font.PLAIN, 14));
                              controlPanel.add(btnNewButton);

                              btnCancel = new JButton("Cancel");
                              btnCancel.setBounds(182, 82, 90, 31);
                              btnCancel.addActionListener(new ActionListener() {

                                
                                public void actionPerformed(ActionEvent arg0) {
                                    // TODO Auto-generated method stub
                                System.exit(0); 
                                }
                            });
                              controlPanel.add(btnCancel);
                              mainFrame.setVisible(true);  
   }

   private class Task extends Thread {    
      public Task(){   }     

    public void run(){       

        PrintStream ps = null;      
        if(filename!=null)
        {
            DateFormat df=new SimpleDateFormat("HH:mm:ss");
            Calendar c=Calendar.getInstance();
            String starttime=df.format(c.getTime());
            System.out.println("Start time: "+starttime);

         try{
//To get current w orking directory
            String workingdirectory=System.getProperty("user.dir");
            System.out.println(workingdirectory);
//Create folder name as LogFolder in workingdirectory
            File file1=new File(workingdirectory+"\\LogFolder\\");

            File f2 = null;         
            //if folder not present we need to create a folder
            if (!file1.exists()) {
                if (file1.mkdir()) {
                    f2=new File(file1, "abc.txt");
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }

            }f2=new File(file1, "abc.txt");
            ps=new PrintStream(f2);

                /* Try to connect to the server on localhost, port 5555 */
            sendinglabel.setVisible(true);
            SwingControlDemo.this.starttime.setText("Start time: "+starttime);

            JOptionPane.showMessageDialog(rootPane, "File is Sending...!!!");






                Socket socket=new Socket("localhost",20001);

                File file=new File(filename);





                double size=(file.length()/(1024*1024));
                //float size1=(float)size;  

                System.out.println("THE FILE SIZE IS "+size);
                //iterate();



                    byte[] mybyte=new byte[(int) file.length()];

                    FileInputStream fileInputStream=new FileInputStream(file);
                    int count;
                    int i=0;
                    OutputStream outputStream=socket.getOutputStream();

                    while((count=fileInputStream.read(mybyte))!=-1)
                    {
                        outputStream.write(mybyte);
                        i+=1;

                        System.out.print("FIle Sent........"+i+"Kilobytes \t");
                        myFun(i, size);



                }
                System.out.println();

                DateFormat df1=new SimpleDateFormat("HH:mm:ss");
                Calendar c1=Calendar.getInstance();
                String endtime=df1.format(c1.getTime());
                System.out.println("End time is : "+endtime);

                JOptionPane.showMessageDialog(rootPane, "File Sent Successfully!!!!\n Start Time is :"+starttime+"\n End Time Is : "+endtime);
                SwingControlDemo.this.endtime.setText(endtime);
                System.out.println("THIS FILE HAS BEEN SENT SUCCESSFULLY!!!");      



                socket.close();
                System.exit(1);



            }

            catch (Exception ex){
                /* Catch any errors */

                JOptionPane.showMessageDialog(rootPane, "Error While Sending File : "+ex.getMessage());
                ex.printStackTrace(ps);

            }
        }
        else
        {
            JOptionPane.showMessageDialog(rootPane, "Please Choose a Valid File");
        }






      }

    private  void myFun(int j,Double mbsize) {
        // TODO Auto-generated method stub

        int per=(int) ((j*100)/mbsize);
        System.out.println("THE INTEGER VALUE IS "+per);
        if(j<mbsize)
        {
            System.out.println("INSIDE IF LOOP");
            progressBar.setValue(per);

        }
        else
        {
            progressBar.setValue(per);

        }
        try {
            Thread.sleep(10);


         } catch (InterruptedException e) {}

      }

    }
   }   