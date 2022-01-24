package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SelectionScreen extends JPanel implements ActionListener {
	
	public static SelectionScreen instance;
	JButton toChess;
	JButton toSnake;
	JButton toConnectFour;
	JButton toMineSweeper;
	JButton toTetris;
	JButton toAbout;
	Font screenFont;
	
	ScreenManager manager;
	FallingStars starAnimation;
	
	SelectionScreen(ScreenManager manager) {
		this.manager = manager;
		instance = this;
		starAnimation = new FallingStars(this);
		
		try {
			screenFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Fonts/NovaSquare-Regular.ttf"));
		} catch (Exception e) {
			screenFont = new Font("Times New Roman", Font.BOLD, 1);
			e.printStackTrace();
		}
		
		setLayout(new GridBagLayout());
		setBackground(Color.BLACK);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(20, 0, 20, 0);
		constraints.fill = GridBagConstraints.BOTH;
		
		screenFont = screenFont.deriveFont(12f);
		
		JLabel title = new JLabel("GAME SELECTION SCREEN");
		title.setFont(screenFont.deriveFont(20f));
		title.setHorizontalAlignment(JLabel.CENTER);
		constraints.gridwidth = 4;
		add(title, constraints);
		
		JLabel about = new JLabel("A Collection Of Games Written In Java by Jacoby Johnson");
		about.setFont(screenFont);
		about.setForeground(Color.WHITE);
		about.setHorizontalAlignment(JLabel.CENTER);
		constraints.gridy = 1;
		add(about, constraints);
		
		JLabel instructions = new JLabel("Press esc at any time to return to this screen");
		instructions.setFont(screenFont);
		instructions.setForeground(Color.WHITE);
		instructions.setHorizontalAlignment(JLabel.CENTER);
		constraints.gridy++;
		add(instructions, constraints);
		
		toChess = new GUIButton("Play Chess");
		constraints.gridwidth = 1;
		constraints.gridy++;
		add(toChess, constraints);
		
		
		toSnake = new GUIButton("Play Snake");
		constraints.gridx = 2;
		add(toSnake, constraints);
		
		
		toConnectFour = new GUIButton("Play Connect Four");
		constraints.gridy++;
		constraints.gridx = 0;
		add(toConnectFour, constraints);
		
		
		toTetris = new GUIButton("Play Tetris");
		constraints.gridx = 2;
		add(toTetris, constraints);
		
		toMineSweeper = new GUIButton("Play MineSweeper");
		constraints.gridy++;
		constraints.gridx = 0;
		constraints.gridwidth = 4;
		add(toMineSweeper, constraints);
		
//		toAbout = new GUIButton("About Page");
//		constraints.gridx = 2;
//		add(toAbout, constraints);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		starAnimation.animate(g2D);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(toChess)) {
			manager.switchScreen(ScreenManager.CHESS);
		} else if (source.equals(toConnectFour)) {
			manager.switchScreen(ScreenManager.CONNECTFOUR);
		} else if (source.equals(toTetris)) {
			manager.switchScreen(ScreenManager.TETRIS);
		} else if (source.equals(toSnake)) {
			manager.switchScreen(ScreenManager.SNAKE);
		} else if (source.equals(toMineSweeper)) {
			manager.switchScreen(ScreenManager.MINESWEEPER);
		}
	}
	
	
	class GUIButton extends JButton {
		
		GUIButton(String text) {
			setFont(screenFont);
			setText(text);
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
			setFocusable(false);
			setBorder(BorderFactory.createEmptyBorder());
			setContentAreaFilled(false);
			addActionListener(SelectionScreen.this);
		}
	}
}

