package mineSweeper;

import java.awt.*;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import main.Main;


public class Message extends JPanel {
	static int layer = 10;
	public Font font;
	public int duration;
	public String title;
	public String message;
	public int lingerTime = 500;
	public int entranceTime = 500;
	public int exitTime = 500;
	public int totalTime;
	private String currentMessage;
	private boolean playing;
	
	JLabel label;
	JTextPane textArea;
	JButton closeButton;
	
	public Message(String title, String message, int duration, boolean addCloseButton) {
		this.title = title;
		this.message = message;
		this.duration = duration;
		this.setOpaque(false);
		this.setSize(Main.layeredPane.getSize());
		this.setLocation(0, -getHeight());
		int borderWidth = Main.layeredPane.getWidth() / 5;
		int borderHeight = Main.layeredPane.getHeight() / 5;
		this.setBorder(BorderFactory.createEmptyBorder(borderHeight, borderWidth, borderHeight, borderWidth));
		this.setLayout(new BorderLayout());
		setFocusable(false);
		
		font = new Font("Times New Roman", Font.BOLD, 12);
		
		label = new JLabel();
		label.setText(title);
		label.setFont(font);
		label.setBackground(Color.BLACK);
		label.setOpaque(true);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setForeground(Color.WHITE);
		label.setPreferredSize(new Dimension(getWidth(), getHeight() / 5));
		
		textArea = new JTextPane();
		textArea.setBackground(Color.BLACK);
		textArea.setFont(font);
		textArea.setForeground(Color.WHITE);
		textArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		textArea.setFocusable(false);
		textArea.setEditable(false);
//		textArea.setLineWrap(true);
//		textArea.setWrapStyleWord(true);
		textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		StyledDocument doc = textArea.getStyledDocument();;
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		closeButton = new JButton();
		closeButton.setBackground(Color.BLACK);
		closeButton.setText("C L O S E");
		closeButton.addActionListener(l -> {
			hideMessage();
		});
		closeButton.setPreferredSize(new Dimension(getWidth(), getHeight() / 5));
		closeButton.setFont(font);
		closeButton.setBackground(Color.BLACK);
		closeButton.setForeground(Color.WHITE);
		
		add(textArea, BorderLayout.CENTER);
		add(label, BorderLayout.NORTH);
		
		if (addCloseButton) {
			add(closeButton, BorderLayout.SOUTH);
		}
		
		totalTime = duration + entranceTime + exitTime + lingerTime;
		
	}
	
	private void hideMessage() {
		playing = false;
		Future<?> exit = Main.scheduler.scheduleAtFixedRate(() -> {
			SwingUtilities.invokeLater(() -> Message.this.setLocation(0, Message.this.getLocation().y - (getHeight() / 30)));
		}, 0, exitTime / 30, TimeUnit.MILLISECONDS);
		
		Main.scheduler.schedule(() -> { 
			exit.cancel(false);
			SwingUtilities.invokeLater(() -> {
				Main.layeredPane.remove(this);
				Main.layeredPane.revalidate(); 
				Main.layeredPane.repaint();
				layer--;
			});
		} , exitTime, TimeUnit.MILLISECONDS);
	}
	
	public void playMessage() {
		if (!playing) {
			totalTime = duration + entranceTime + exitTime + lingerTime;
			playing = true;
			Main.layeredPane.add(this, Integer.valueOf(layer));
			Main.layeredPane.revalidate();
			Main.layeredPane.repaint();
			layer++;
			
			//entrance animation;
			Future<?> entrance = Main.scheduler.scheduleAtFixedRate(() -> {
				SwingUtilities.invokeLater(() -> Message.this.setLocation(0, Message.this.getLocation().y + (getHeight() / 30)));
			}, 0, entranceTime / 30, TimeUnit.MILLISECONDS);
			Main.scheduler.schedule(() -> { entrance.cancel(false); } , entranceTime, TimeUnit.MILLISECONDS);
			
			
			//prints text across box
			Future<?> future = Main.scheduler.scheduleAtFixedRate(() -> {
				if (textArea.getText().length() < message.length()) {
				textArea.setText(message.substring(0, textArea.getText().length() + 1));
				}
			}, entranceTime, duration / (message.length()), TimeUnit.MILLISECONDS);
			
			Main.scheduler.schedule( () -> future.cancel(false), entranceTime + duration, TimeUnit.MILLISECONDS);
			
			Main.scheduler.schedule(() -> {
				if (Message.this.isShowing() && playing == true) {
					System.out.println("hide");
					hideMessage();
				}
			}, entranceTime + duration + lingerTime, TimeUnit.MILLISECONDS);
			
		}
	}
	
	

}
