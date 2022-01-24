package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;

import main.ScreenManager;

public class SnakeGame extends JPanel implements KeyListener {
	Snake snake;
	Position apple;
	int score;
	public JLabel scorePanel;
	Color[] backgroundColors;
	boolean started;
	
	char currentDirection;
	int xDirection;
	int yDirection;
	
	public Dimension size;
	int rows;
	int cols;
	int squareSize;
	Timer timer;
	ScreenManager manager;
	
	public SnakeGame(ScreenManager manager) {
		this.manager = manager;
		
		addKeyListener(this);
		addKeyListener(manager);
		setFocusable(true);
		requestFocus(true);
		rows = 20;
		cols = 30;
		
		setBackground(Color.GREEN);
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		scorePanel = new JLabel();
		scorePanel.setOpaque(true);
		scorePanel.setBackground(Color.BLACK);
		scorePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.WHITE, Color.lightGray));
		scorePanel.setHorizontalTextPosition(JLabel.CENTER);
		scorePanel.setForeground(Color.WHITE);
		scorePanel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		
		addAncestorListener(new main.AncestorAdapter() {
			public void ancestorAdded(AncestorEvent event) {
				add(scorePanel, BorderLayout.NORTH);
				revalidate(); repaint();
				requestFocus();
				if (!started) {
					start();
				} else {
					timer.restart();
				}
			}
			
			public void ancestorRemoved(AncestorEvent event) {
				remove(scorePanel);
				revalidate(); repaint();
				if (timer.isRunning()) {
					timer.stop();
				}
			}
		});
		
		addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent event) {
        		scorePanel.setPreferredSize(new Dimension(getWidth(), getHeight() / 8));
//        		sideBar.setSize(sideBar.getPreferredSize());
        		revalidate(); repaint();
        	}
        });
	}
	
	public void start() {
		setVisible(true);
		requestFocus();
		started = true;
		score = 0;
		apple = new Position((int) (rows / 2), (int) (cols * 0.75), true);
		scorePanel.setText("Score: " + score);
		backgroundColors = new Color[] {new Color(155, 255, 155), new Color(100, 225, 100)};
		
		snake = new Snake();
		for (int i = 0; i < 5; i++) {
			snake.add(new Position(rows / 2, (6 - i)));
		}
		
		currentDirection = 'd';
		xDirection = 1;
		yDirection = 0;
		
		timer = new Timer(100, e -> iterateGame());
		timer.start();
	}
	
	public void end() {
		timer.stop();
		started = false;
		System.out.println("Game Over");
		scorePanel.setText("Score: " + score + "  " + "GAME OVER!");
		backgroundColors = new Color[] {new Color(225, 155, 155), new Color(225, 100, 100)};
		repaint(); 
		
		int response = JOptionPane.showConfirmDialog(this, "WOULD YOU LIKE TO REPLAY?");
		if (response == 0) {
			start();
		}
	}
	
	protected void paintComponent(Graphics g) { //col is x, row is y
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		int bouncer = 0;
		squareSize = Math.min(getWidth() * 9 / 10, getHeight() * 9 / 10) / Math.max(rows, cols);
		int paddingX = (getWidth() - squareSize * cols) / 2;
		int paddingY = (getHeight() - squareSize * rows) / 2;
		
		for (int row = 0; row <= rows; row++) {
			bouncer = row % 2;
			for (int col = 0; col <= cols; col++) {
				g2D.setPaint(backgroundColors[bouncer % backgroundColors.length]); bouncer++;
				g2D.fillRect(col * squareSize + paddingX, row * squareSize + paddingY, squareSize, squareSize);
			}
		}
		
		
		snake.drawSnake(g2D, paddingX, paddingY);
		
		g2D.setPaint(Color.RED);
		g2D.fillRect(apple.getCol() * squareSize + paddingX, apple.getRow() * squareSize + paddingY, squareSize, squareSize);
	}
	
	public void iterateGame() {
		snake.moveSnake();
		Position head = (Position) snake.getFirst();
		
		if (apple.equals(head)) {
			snake.extend();
			moveApple();
			//repaint(apple.getCol() * squareSize, apple.getRow() * squareSize, squareSize, squareSize);
			score++;
			scorePanel.setText("Score: " + score);
		}
		
		int headRow = head.getRow();
		int headCol = head.getCol();
		
		if (headRow >= getHeight() / squareSize || headCol >= getWidth() / squareSize || headRow < 0 || headCol < 0) {
			end();
		}
		
		
		//Rectangle bounds = snake.getBounds();
		//repaint((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(),(int) bounds.getHeight());
		repaint();
	}
	
	private void moveApple() {
		apple = new Position((int) (Math.random() * rows), (int) (Math.random() * cols), true);
		if (snake.checkEquals(apple)) {
			System.out.println("INVALID");
			moveApple(); return;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		char move = e.getKeyChar();
		if (move == currentDirection || move == oppositeDirection(currentDirection)) { return; }
		
		switch (move) {
		case 'w': xDirection = 0; yDirection = -1; break;
		case 'a': xDirection = -1; yDirection = 0; break;
		case 's': xDirection = 0; yDirection = 1; break;
		case 'd': xDirection = 1; yDirection = 0; break;
		}
		
		currentDirection = move;
		
	}
	
	private char oppositeDirection(char move) {
		switch (move) {
		case 'w': return 's';
		case 'a': return 'd';
		case 's': return 'w';
		case 'd': return 'a';
		}
		return '?';
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class Position {
		private int row;
		private int col;
		boolean apple;
		
		Position(int row, int col) {
			this.row = row;
			this.col = col;
			apple = false;
		}
		
		Position(int row, int col, boolean isApple) {
			this.row = row;
			this.col = col;
			apple = isApple;
		}
		
		
		public void setApple() { apple = true; }
		public void blank() { apple = false; }
		public int getRow() { return row;	}
		public int getCol() { return col; }
		
		public void setPos(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
		public boolean equals(Position pos) {
			if (pos.getRow() == row && pos.getCol() == col) {
				return true;
			} return false;
		}
		
	}
	
	class Snake {
		private SnakePart first;
		private SnakePart last;
		private boolean addOnMove;
		private int size;
		
		Snake() {
			size = 0;
			addOnMove = false;
		}
		
		public int size() { return size; }
		public SnakePart getFirst() { return first; }
		public void extend() { addOnMove = true; }
		
		public SnakePart getLast() { return last; }
		
		
		public void moveSnake() {
			int prevRow = first.getRow();
			int prevCol = first.getCol();
			SnakePart current = first.next;
			first.setPos(prevRow + yDirection, prevCol + xDirection);
			int currentRow;
			int currentCol;
			
			while (current.next != null) {
				currentRow = current.getRow();
				currentCol = current.getCol();
				current.setPos(prevRow, prevCol);
				
				if (first.equals(current)) {
					if (current != first.next) {
						end();
					}
				}
				
				prevRow = currentRow;
				prevCol = currentCol;
				current = current.next;
			}
			
			currentRow = current.getRow();
			currentCol = current.getCol();
			current.setPos(prevRow, prevCol);
			
			if (addOnMove) {
				System.out.println("apple eaten");
				add(new Position(currentRow, currentCol));
				addOnMove = false;
			}
		}
		
		
		public void add(Position p) {
			SnakePart added = new SnakePart(p.getRow(), p.getCol());
			size++;
			
			if (last == null) {
				first = added;
				last = added; return;
			}
			
			last.next = added;
			last = added;
		}
		
		public boolean checkEquals(Position p) {
			SnakePart current = first;
			
			while (current.next != null) {
				if (p.getRow() == current.getRow() && p.getCol() == current.getCol()) {
					return true;
				}
				current = current.next;
			}
			if (p.getRow() == current.getRow() && p.getCol() == current.getCol()) {
				return true;
			}
			return false;
		}
		
		public Rectangle getBounds() {
			int top; int left; int right; int bottom;
			SnakePart current = first;
			top = first.getRow();
			left = first.getCol();
			bottom = top;
			right = left;
			
			while (current.next != null) {
				int currentRow = current.getRow();
				int currentCol = current.getCol();
				
				if (currentRow < top) { top = currentRow; }
				if (currentRow > bottom) { bottom = currentRow; }
				if (currentCol < left) { left = currentCol; }
				if (currentCol > right) { right = currentCol; }
				current = current.next;
				}
			int currentRow = current.getRow();
			int currentCol = current.getCol();
			
			if (currentRow < top) { top = currentRow; }
			if (currentRow > bottom) { bottom = currentRow; }
			if (currentCol < left) { left = currentCol; }
			if (currentCol > right) { right = currentCol; }
			
			Rectangle bounds = new Rectangle( (left - 1) * squareSize, (top - 1) * squareSize, (right + 1) * squareSize, (bottom + 1) * squareSize);
			return bounds;
			}
		
		public void drawSnake(Graphics2D g2D, int paddingX, int paddingY) {
			SnakePart current = first;
			
			while (current.next != null) {
				g2D.setPaint(Color.GREEN);
				g2D.fillRect(current.getCol() * squareSize + paddingX, current.getRow() * squareSize + paddingY, squareSize, squareSize);
				g2D.setPaint(Color.BLACK);
				g2D.drawRect(current.getCol() * squareSize + paddingX, current.getRow() * squareSize + paddingY, squareSize, squareSize);
				if (current.equals(first)) {
					int eyeSize = squareSize / 5;
					int middleX = (current.getCol() + 1) * squareSize - (squareSize / 2);
					int middleY = (current.getRow() + 1) * squareSize - (squareSize / 2);
					int offsetX = (int) Math.abs((eyeSize) * yDirection);
					int offsetY = (int) Math.abs((eyeSize) * xDirection);
					g2D.fillRect(middleX - offsetX + paddingX, middleY - offsetY + paddingY, eyeSize, eyeSize);
					g2D.fillRect(middleX + offsetX + paddingX, middleY + offsetY + paddingY, eyeSize, eyeSize);
				}
				
				current = current.next;
			}
			g2D.setPaint(Color.GREEN);
			g2D.fillRect(current.getCol() * squareSize + paddingX, current.getRow() * squareSize + paddingY, squareSize, squareSize);
			g2D.setPaint(Color.BLACK);
			g2D.drawRect(current.getCol() * squareSize + paddingX, current.getRow() * squareSize + paddingY, squareSize, squareSize);
		}
		
	}
	
	class SnakePart extends Position {
		SnakePart next;
		
		SnakePart(int row, int col) {
			super(row, col);
			next = null;
		}
		
		SnakePart(int row, int col, SnakePart next) {
			super(row, col);
			this.next = next;
		}
	}
	
	
	

}
