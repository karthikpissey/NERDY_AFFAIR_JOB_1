package com.infy.hackathon.na.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.infy.hackathon.na.processor.ResourceUtil;

public class SwingControlServer extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SwingControlServer frame = new SwingControlServer();
                    frame.setVisible(false);
                    frame.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected static void start() throws IOException {
        // TODO Auto-generated method stub
         System.out.println("Server running...");

            /* Listen on port 5555 */

            ServerSocket server = new ServerSocket(20001);

            /* Accept the sk */

            Socket socket = server.accept();

            System.out.println("Server accepted client");
            InputStream inputStream=socket.getInputStream();
            byte[] readbyte=new byte[(1024*10)*1024];           


            FileOutputStream fileOutputStream=new FileOutputStream("C:/Users/shaswat/Desktop/shapanda.zip");
            int writebyte;
            int count=0;
            while((writebyte=inputStream.read(readbyte))!=-1)
            {
                if(writebyte>0)
                    count+=writebyte;
                fileOutputStream.write(readbyte, 0, writebyte);
            }

            //System.out.println("THE WRITEBYTE VALUE IS "+writebyte+"THE READ BYTE VALUE IS"+count);
            inputStream.close();
            System.out.println("CONNECTION CLOSED");
            ResourceUtil util = new ResourceUtil();
            util.createProjectResource();


    }

    /**
     * Create the frame.
     */
    public SwingControlServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);


    }


}