package main;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;


public class Main {
	public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	public static JLayeredPane layeredPane;
	public static JFrame gameFrame;
	
	public static void main(String[] args) {
		gameFrame = new JFrame();
		gameFrame.setSize(500, 500);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setResizable(true);
		gameFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		gameFrame.getContentPane().setBackground(new java.awt.Color(20, 20, 20));
		layeredPane = gameFrame.getLayeredPane();
		
		JPanel gamePanel = new JPanel();
		gamePanel.setPreferredSize(new Dimension(gameFrame.getWidth() * 9 / 10, gameFrame.getHeight() * 9 / 10));
		gamePanel.setSize(gamePanel.getPreferredSize());
		gamePanel.setBackground(java.awt.Color.BLACK);
		
		gameFrame.add(gamePanel);
		gameFrame.pack();
		gameFrame.setVisible(true);
		
		gameFrame.addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent event) {
        		int bounds = Math.min(gameFrame.getWidth(), gameFrame.getHeight()) * 9 / 10;
        		gamePanel.setPreferredSize(new Dimension(bounds, bounds));
        		gameFrame.revalidate(); gameFrame.repaint();
        		gamePanel.revalidate(); gamePanel.repaint();
        	}
        });
		
		new ScreenManager(gamePanel);
	}

}