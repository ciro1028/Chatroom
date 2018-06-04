/*******************
 * Ciro Amaral
 * CIST 2372
 * Date 04/30/2017
 * Final Project
 * 
********************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

//chat program main class
public class ClientChat extends JFrame{
	public static void main(String[] args) {
		new ClientChat();
		
		Runnable InputThread = new input();
		Thread thread1 = new Thread(InputThread);
		thread1.start();
		
		Runnable OutputThread = new output();
		Thread thread2 = new Thread(OutputThread);
		thread2.start();	
	}
	
	//creating objects
	JFrame frame = new JFrame();
	JPanel chatPanel = new JPanel();
	JPanel writingPanel = new JPanel();
	static JTextField textField = new JTextField("", 30);
	
	static JTextArea textArea = new JTextArea(9, 34);
	static JScrollPane scroll = new JScrollPane(textArea);
	
	static JButton sendButton = new JButton();
	static Socket connection;
	
	public ClientChat(){
		//setting up frame
		this.setSize(450, 220);
		this.setLocationRelativeTo(null);
		this.setTitle("Chatroom - Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(chatPanel, BorderLayout.PAGE_START);
		this.add(writingPanel, BorderLayout.PAGE_END);
		
		//setting up closing operation when red x is pressed
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
	        int localVar = JOptionPane.showConfirmDialog(null, "Are you sure you want to close this window?", 
	        		"Dialog", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		      if(localVar == JOptionPane.YES_OPTION){
		          System.exit(0);
		     }
		    }
		});
		
		//setting up chat panel
		chatPanel.add(scroll);
		chatPanel.setBorder(BorderFactory.createBevelBorder(NORMAL));
		
		textArea.setEditable(false);
	    textArea.setLineWrap(true);
	    textArea.setVisible(true);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    
		
		//setting up writing panel
		writingPanel.setLayout(new GridBagLayout());
		writingPanel.setBorder(BorderFactory.createBevelBorder(NORMAL));
		writingPanel.add(textField);
		writingPanel.add(sendButton);
		sendButton.setText("Send");
		sendButton.setSize(10, 5);
		this.setVisible(true);
		
		//setting up connection
		try{
			connection = new Socket("localhost", 8000);
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}	
	}
	
	static class output implements Runnable{
		public void run(){
			class ButtonListener implements ActionListener{
				public void actionPerformed(ActionEvent e){
					if(e.getSource() == sendButton || e.getSource() == textField){
						try{			
							PrintStream ps = new PrintStream(connection.getOutputStream());
							ps.println("Client says: " + textField.getText());
						}
						catch (IOException ex){
						}
						
						textArea.append("Client: " + textField.getText() + "\n");
						textField.setText(null);
						textArea.setCaretPosition(textArea.getDocument().getLength());
					}
				}
			}
			ButtonListener buttonl = new ButtonListener();
			sendButton.addActionListener(buttonl);
			textField.addActionListener(buttonl);
		}
	}
	
	static class input implements Runnable{
		public void run(){
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while(true){
					String string = br.readLine();
					textArea.append(string + "\n");
					textArea.setCaretPosition(textArea.getDocument().getLength());
					}
			}
			catch (IOException e){
				
			}
		}
	}
}
