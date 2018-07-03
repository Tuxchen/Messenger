package de.tuxchan;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class ChatClient extends JFrame {
	private Container c;
	private JTextField nick;
	private JTextArea chat;
	private JScrollPane sp;
	private JTextField input;
	private final Font font = new Font("Comic Sans", Font.BOLD + Font.ITALIC, 14);
	
	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private Thread t;
	private String text;
	
	private class ChatThread extends Thread {
		
		public void run() {
			
			while(true) {
				try {
					text = in.readLine();
					if(text != null) {
						chat.append(text + "\n");
						chat.setCaretPosition(chat.getText().length());
					}
				}
				catch(IOException e) {
					JOptionPane.showMessageDialog(c, e.getMessage());
				}
				
			}
			
		}
	}
	
	private class WindowEvents extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				out.close();
				in.close();
				client.close();
			}
			catch(IOException exp) {
				JOptionPane.showMessageDialog(c, exp.getMessage());
			}
		}
	}
	
	private class PressEnter implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == input) {
				chat.append("<" + nick.getText() + "> " + input.getText() + "\n");
				chat.setCaretPosition(chat.getText().length());
				out.println("<" + nick.getText() + "> " + input.getText());
				input.setText("");
			}
		}
	}
	
	public ChatClient() {
		c = getContentPane();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(c, e.getMessage());
		}
		
		nick = new JTextField("Anonymous");
		nick.setFont(font);
		
		chat = new JTextArea();
		chat.setEditable(false);
		chat.setLineWrap(true);
		chat.setWrapStyleWord(true);
		chat.setFont(font);
		sp = new JScrollPane(chat);
		
		input = new JTextField();
		input.setFont(font);
		input.addActionListener(new PressEnter());
		
		c.add(BorderLayout.NORTH, nick);
		c.add(BorderLayout.CENTER, sp);
		c.add(BorderLayout.SOUTH, input);
		
		chat.setText("*** Warte auf Chat-Partner ***\n");
		chat.setCaretPosition(chat.getText().length());
		
		t = new ChatThread();
		t.start();
		
		try {
			client = new Socket("tuxchan.de", 3000);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(c, e.getMessage());
			System.exit(1);
		}
	}
	
	public static void main (String[] args) {
		ChatClient window = new ChatClient();
		window.setTitle("ChatClient 1.0");
		window.setSize(500, 600);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}