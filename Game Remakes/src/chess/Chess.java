package chess;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;

import main.ScreenManager;

@SuppressWarnings("serial")
public class Chess extends JPanel {
	
	private Piece[][] board;
	Dimension dimensions;
	SidePanel sidePanel;
	Piece input;
	Piece selectedPosition;
	int currentPlayer;
	private int direction;
	Piece hovering;
	Piece[] possibleMoves;
	int squareSize;
	boolean finished;
	
	ScreenManager manager;
	Mouse mouse;
	
	public Chess(ScreenManager manager) {
		this.manager = manager;
		JPanel parent = manager.getDisplay();
		sidePanel = new SidePanel();
		Images.init();
		
		start();
		//setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
		mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		sidePanel.setPreferredSize(new Dimension((int) this.getWidth() / 5, (int) parent.getHeight()));
		squareSize = (parent.getWidth() - sidePanel.getWidth()) / 8;
		//parent.setSize(sidePanel.getWidth() + getWidth(), parent.getHeight());
		
		setFocusable(true);
		addKeyListener(manager);
		addAncestorListener(new main.AncestorAdapter() {
			public void ancestorAdded(AncestorEvent event) {
//				System.out.println(getSize());
				requestFocus();
			}
			
			public void ancestorRemoved(AncestorEvent event) {
				
			}
		});
	}
	
	private void start() {
		constructBoard();
		sidePanel.createTimer();
		currentPlayer = 1;
		sidePanel.updateTurn();
		direction = currentPlayer == 1 ? 1 : -1;
		selectedPosition = null;
		possibleMoves = null;
		finished = false;
		input = null;
		hovering = null;
	}
	
	private void constructBoard() {
		board = new Piece[8][8];
		int playerSide = 2;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = new Piece(row, col);
			}
		}
		
		//Pieces
		for (int row = 0; row < board.length; row++) {
			playerSide = row < board.length / 2 ? 2 : 1;
			if (row == 0 || row == board.length - 1) {
				board[row][0] = new Rook(row, 0, playerSide);
				board[row][1] = new Knight(row, 1, playerSide);
				board[row][2] = new Bishop(row, 2, playerSide);
				board[row][3] = new King(row, 3, playerSide);
				board[row][4] = new Queen(row, 4, playerSide);
				board[row][5] = new Bishop(row, 5, playerSide);
				board[row][6] = new Knight(row, 6, playerSide);
				board[row][7] = new Rook(row, 7, playerSide);
			} else {
				for (int col = 0; col < board[row].length; col++) {
					board[row][col] = new Pawn(row, col, playerSide);
				}
				row = row == 1 ? board.length - 3 : row; 
				
			}
		}
		
	}
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		Color[] colors = {new Color(245, 245, 220), new Color(101, 67, 33)};
		squareSize = this.getWidth() / 8;
		BasicStroke borderStroke = new BasicStroke(2);
		Paint borderPaint = Color.BLACK;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				Piece currentPosition = board[row][col];
				colors[0] = currentPlayer == 1 ? Color.WHITE : Color.GRAY;
				int boxX = col * squareSize;
				int boxY = row * squareSize;
				borderStroke = new BasicStroke(2);
				g2D.setPaint(colors[ (row + col) % 2 ]);
				borderPaint = Color.BLACK;
				
				if (hovering != null) {
					if (hovering.equals(currentPosition)) {
						borderPaint = finished ? Color.MAGENTA : Color.GRAY;
						borderStroke = new BasicStroke(3);
						g2D.setPaint(finished ? Color.PINK : Color.lightGray);
					}
				}
				
				if (input != null) {
					if (input.equals(currentPosition)) {
						borderPaint = Color.GREEN;
						borderStroke = new BasicStroke(3);
						g2D.setPaint(new Color(144, 238, 144));
					}
				}
				
				if (possibleMoves != null && input != null) {
					for (Piece possibleMove : possibleMoves) {
						if (possibleMove.equals(currentPosition)) {
							borderPaint = Color.BLUE;
							borderStroke = new BasicStroke(3);
							g2D.setPaint(new Color(144, 144, 238));
						}
					}
				}
				
				g2D.setStroke(new BasicStroke(1));
				g2D.fillRect(boxX, boxY, squareSize, squareSize);
				g2D.drawImage(currentPosition.getImage(), boxX, boxY, squareSize, squareSize, null);
				
				g2D.setStroke(borderStroke);
				g2D.setPaint(borderPaint);
				g2D.drawRect(boxX, boxY, (int) (squareSize - borderStroke.getLineWidth()), (int) (squareSize - borderStroke.getLineWidth()));
				
			}
		}
	}
	
	public void move(Piece p1, Piece p2) throws IOException {
		System.out.println("Move registered");
		int p1Row = p1.getRow();
		int p1Col = p1.getCol();
		
		if (p1 instanceof Pawn) {
			Pawn pawn = (Pawn) p1;
			if (pawn.firstTurn == true) { pawn.firstTurn = false; }
		}
		
		p1.setRow(p2.getRow());
		p1.setCol(p2.getCol());
		board[p2.getRow()][p2.getCol()] = p1;
		check(p1);
		
		board[p1Row][p1Col] = new Piece(p1Row, p1Col);
		possibleMoves = null;
		input = null;
		
		if (p2 instanceof King) {
			win(); return;
		}
		
		if (!finished) {
			switchPlayer();
		}
	}
	
	public void check(Piece p1) {
		Piece[] movesfromP1 = p1.getPossibleMoves(this);
		
		for (Piece move : movesfromP1) {
			if (move instanceof King) {
				System.out.println("Check!");
			}
		}
	}
	
	public void win() throws IOException {
		System.out.println("PLAYER " + currentPlayer + " WINS!!!");
		finished = true;
		sidePanel.endTimer();
		int response = JOptionPane.showConfirmDialog(null, "PLAYER " + currentPlayer + " WINS!!!" + "Would you like to Play Again");
		if (response == 0) {
			start();
		}
	}
	
	public void switchPlayer() {
		currentPlayer = currentPlayer == 1 ? 2 : 1;
		direction = currentPlayer == 1 ? 1 : -1;
		sidePanel.updateTurn();
	}
	
	public boolean checkMoveInput(Piece in) {
		try {
			System.out.println("Checking");
			if (Arrays.asList(possibleMoves).contains(in)) {
				return true;
			}
		} catch (NullPointerException n) {
			return false;
		} return false;
	}
	
	public Piece[][] getBoard() {
		return board;
	}
	
	public int getDirection() {
		return direction;
	}
	
	class Mouse extends MouseAdapter {
		Mouse() {}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!finished) {
				double x = e.getX();
				double y = e.getY();
				
				System.out.println("X: " + x + "Y: " + y);
				int row = (int) y / squareSize;
				int col = (int) x / squareSize;
				input = board[row][col];
				input.print();
				
				if (possibleMoves != null) {
					if (input.getPlayer() != currentPlayer && !Arrays.asList(possibleMoves).contains(input)) {
						input = null; 
						possibleMoves = null;
						return; 
					}
				} else if (input.getPlayer() != currentPlayer) { //no possible moves and is not clicking on an ally
					input = null; 
					return; 
				}
				
				if (checkMoveInput(input)) {
					try {
						move(selectedPosition, input);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					possibleMoves = input.getPossibleMoves(Chess.this);
					selectedPosition = input;
				}
				repaint();
			}
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			Chess.this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Chess.this.setBorder(BorderFactory.createEmptyBorder());
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			double x = e.getX();
			double y = e.getY();
			
			Piece currentlyHovering;
			int row = (int) y / squareSize;
			int col = (int) x / squareSize;
			try { 
				currentlyHovering = board[row][col];
			} catch (ArrayIndexOutOfBoundsException exec) {
				return;
			}
			
			try {
				if (!currentlyHovering.equals(hovering))
					hovering = board[row][col];
			} catch (NullPointerException n) {
				hovering = board[row][col];
			}
			
			repaint();
		}
	}


	
	/*
	 * 
	 * 
	 * 
	 * SEPARATE PANEL GUIS
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public JPanel getSidePanel() { return sidePanel; }
	
	class SidePanel extends JPanel {
		
		Border defaultLabelBorder;
		javax.swing.Timer timer;
		JPanel player1Captured;
		JPanel player2Captured;
		JLabel timerPanel;
		JLabel Turns;
		JLabel playerTurn;
		int minutes;
		String time;
		
		
		SidePanel() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
			defaultLabelBorder = BorderFactory.createLineBorder(Color.WHITE, 3, true);
			setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
			playerTurn = new JLabel();
			setDefaultPanelConfigs(playerTurn);
			playerTurn.setText("Player " + currentPlayer);
			
			timerPanel = new JLabel();
			time = "0:00";
			minutes = 0;
			setDefaultPanelConfigs(timerPanel);
			timerPanel.setText(time);
			
			add(playerTurn);
			add(timerPanel);
		}
		
		public void createTimer() {
			int delay = 1000;
			timer = new javax.swing.Timer(delay, e -> addToTimer());
			timer.start();
		}
		
		private void setDefaultPanelConfigs(JLabel label) {
			label.setSize(new Dimension(getWidth(), getHeight() / 10));
			label.setOpaque(true);
			label.setBackground(Color.BLACK);
			label.setForeground(Color.WHITE);
			label.setBorder(defaultLabelBorder);
			label.setFont(new Font("Times New Roman", Font.BOLD, 18));
		}
		
		private void addToTimer() {
			minutes++;
			String mins = (minutes % 60) < 10 ? "0" + (minutes % 60) : Integer.toString(minutes % 60);
			time = (minutes / 60) + ":" + mins;
			timerPanel.setText(time);
		}
		
		public void endTimer() {
			timer.stop();
			minutes = 0;
		}
		
		public void updateTurn() {
			playerTurn.setText("Player " + currentPlayer);
		}
		
	}
	
	
}
