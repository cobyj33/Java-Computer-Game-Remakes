package mineSweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;

import main.Main;
import main.ScreenManager;

public class MineSweeper extends JPanel implements MouseListener, MouseMotionListener {
	int[][] board;
	int[][] visibleBoard;
	int boardRows = 10;
	int boardCols = 10;
	int squareSize = 15;
	int numOfMines = 5;
	int covered = 0;
	boolean inGame;
	BottomBar bottomBar;
	HashMap<Integer, Color> colorMap;
	ScreenManager manager;
	public static final int BLANK = -1, MINE = -2, FLAG = -3, COVERED = -4;

	public MineSweeper(ScreenManager manager) {
		this.manager = manager;
		addKeyListener(manager);
		bottomBar = new BottomBar();
		colorMap = new HashMap<>();
		board = new int[boardRows][boardCols];
		visibleBoard = new int[boardRows][boardCols];
		setBackground(Color.BLACK);
		setFocusable(true);
		
		colorMap.put(BLANK, Color.lightGray);
		colorMap.put(MINE, Color.RED);
		colorMap.put(FLAG, Color.BLACK);
		colorMap.put(COVERED, new Color(50, 50, 50));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		inGame = true;
		constructBoard();
		
		addAncestorListener(new main.AncestorAdapter() {
			public void ancestorAdded(AncestorEvent event) {
				requestFocus();
			}
			
			public void ancestorRemoved(AncestorEvent event) {
				
			}
		});
	}
	
	public void constructBoard() {
		board = new int[boardRows][boardCols];
		visibleBoard = new int[boardRows][boardCols];
		covered = boardRows*boardCols;
		
		for (int row = 0; row < visibleBoard.length; row++) {
			for (int col = 0; col < visibleBoard[row].length; col++) {
				visibleBoard[row][col] = COVERED;
			}
		}
		
		//First, place mines;
		Random randomizer = new Random();
		for (int m = 0; m < numOfMines; m++) {
			int mineRow = randomizer.nextInt(boardRows);
			int mineCol = randomizer.nextInt(boardCols);
			board[mineRow][mineCol] = MINE;
		}
		
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				
				if (board[row][col] != MINE) {
					board[row][col] = getNumOfMinesAround(row, col);
				}
				
			}
		}
		
		repaint();
		
	}
	
	private int getNumOfMinesAround(int row, int col) {
		int mines = 0;
		
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				
				try {
					if (board[r][c] == MINE) mines++;
				} catch (ArrayIndexOutOfBoundsException e) {
					continue;
				}
				
			}
		}
		
		return mines;
	}
	
	private void uncover(int row, int col) {
		visibleBoard[row][col] = board[row][col];
		covered--;
		System.out.println("Uncovered: " + board[row][col]);
		
		if (visibleBoard[row][col] == 0) {
			for (int r = row - 1; r <= row + 1; r++) {
				for (int c = col - 1; c <= col + 1; c++) {
					
					try {
					if (board[r][c] == 0 && visibleBoard[r][c] == COVERED)
						uncover(r, c);
					} catch (ArrayIndexOutOfBoundsException e) {
						continue;
					}
					
				}
			}
		}
		
		
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		
		if (this.getSize() != null && bottomBar.getSize() == null) {
			bottomBar.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() / 8));
			bottomBar.setSize(bottomBar.getSize());
			this.revalidate();
			this.repaint();
			return;
		}
		
		if (getWidth() / visibleBoard[0].length > (getHeight() - bottomBar.getHeight()) / visibleBoard.length) {
			squareSize = (getHeight() - bottomBar.getHeight()) / visibleBoard.length ;
		} else {
			squareSize = getWidth() / visibleBoard[0].length;
		}
		
		
		FontMetrics metrics = g2D.getFontMetrics();
		
		while ( !(metrics.stringWidth("0") > squareSize || metrics.getHeight() > squareSize)) {
			g2D.setFont(g2D.getFont().deriveFont( g2D.getFont().getSize2D() + 1f));
			metrics = g2D.getFontMetrics();
		}
		g2D.setFont(g2D.getFont().deriveFont( g2D.getFont().getSize2D() - 1f));
		
		

		for (int row = 0; row < visibleBoard.length; row++) {

			for (int col = 0; col < visibleBoard[row].length; col++) {

				
				int position = visibleBoard[row][col];
				
				if (position <= 0) {
					switch (position) {
					case 0: 
					case BLANK: g2D.setPaint(colorMap.get(BLANK)); break;
					case MINE: g2D.setPaint(colorMap.get(MINE)); break;
					case FLAG: g2D.setPaint(colorMap.get(FLAG)); break;
					case COVERED: g2D.setPaint(colorMap.get(COVERED)); break;
					}
					
					g2D.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
					
				} else {
					
					g2D.setPaint(colorMap.get(BLANK));
					g2D.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
					g2D.setPaint(colorMap.get(MINE));
					String number = Integer.toString(visibleBoard[row][col]);
					g2D.drawString(number, col * squareSize + (squareSize / 2) - (metrics.stringWidth(number) / 2), row * squareSize + (int) (squareSize / 1.25));
					
				}
				
				g2D.setPaint(Color.BLACK);
				g2D.drawRect(col * squareSize, row * squareSize, squareSize, squareSize);

				
			}
		}
	}
	
	public void endGame() {
		if (inGame) {
			inGame = false;
			board = visibleBoard;
			repaint();
			Message message;
			
			if (covered != numOfMines) {
				System.out.println("YOU LOSE!");
				message = new Message("GAME RESULTS", "YOU LOSE", 1000, false);
			} else {
				System.out.println("YOU WIN!");
				message = new Message("GAME RESULTS", "YOU WIN", 1000, false);
			}
			message.playMessage();
			
			Main.scheduler.schedule(() -> { constructBoard(); inGame = true; }, message.totalTime, TimeUnit.MILLISECONDS);
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePressed(e);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int row = e.getY() / squareSize;
		int col = e.getX() / squareSize;
		
		if (board[row][col] != visibleBoard[row][col]) {
			uncover(row, col);
		}
		System.out.println("selected: " + board[row][col] + " Row: " + row + " Col: " + col);
		
		if (board[row][col] == MINE || covered == numOfMines) {
			endGame();
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}

class BottomBar extends JPanel {
	
	BottomBar() {
		
	}
}
