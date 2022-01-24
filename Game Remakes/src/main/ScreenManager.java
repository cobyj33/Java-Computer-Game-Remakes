package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import chess.Chess;
import connectFour.Connect4;
import mineSweeper.MineSweeper;
import snake.SnakeGame;
import tetris.TetrisGame;


public class ScreenManager implements KeyListener {
	JPanel displayPanel;
	public static final int SELECTIONSCREEN = 0,
			CHESS = 1, 
			CONNECTFOUR = 2,
			MINESWEEPER = 3,
			SNAKE = 4,
			TETRIS = 5;
	
	HashMap<Integer, JPanel> screens;
	int currentScreen;
	
	
	public ScreenManager(JPanel displayPanel) {
		screens = new HashMap<>();
		this.displayPanel = displayPanel;
		
		screens.put(SELECTIONSCREEN, new SelectionScreen(this));
		screens.put(CHESS, new Chess(this));
		screens.put(CONNECTFOUR, new Connect4(this));
		screens.put(MINESWEEPER, new MineSweeper(this));
		screens.put(SNAKE, new SnakeGame(this));
		screens.put(TETRIS, new TetrisGame(this));
		
		//starting screen
		currentScreen = SELECTIONSCREEN;
		switchScreen(currentScreen);
	}
	
	public void addScreen(int id, JPanel screen) {
		screen.setPreferredSize(displayPanel.getSize());
		screens.put(id, screen);
	}
	
	public void switchScreen(int screenID) {
		SwingUtilities.invokeLater( () -> {
			
		if (screens.containsKey(screenID)) {
			
			displayPanel.setLayout(new java.awt.FlowLayout());
			System.out.println(screenID);
			System.out.println("Switching screens");
			displayPanel.remove(screens.get(currentScreen));
			JPanel nextScreen = screens.get(screenID);
			nextScreen.setPreferredSize(displayPanel.getSize());
			
			displayPanel.add(screens.get(screenID));
			displayPanel.revalidate(); displayPanel.repaint();
			currentScreen = screenID;
			
			
		} else {
			System.out.println("[ScreenManager]: Invalid Screen");
		}
			
		});
	}
	
	public JPanel getDisplay() {
		return displayPanel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: switchScreen(SELECTIONSCREEN); break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}	
}