package connectFour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;

import main.ScreenManager;

public class Connect4 extends JPanel {

    String currentPlayer;
    String[][] logicBoard;
    Container parent = this.getParent();
    HashMap<String, Color> PlayerColors;
    JLabel topLabel;
    
    boolean win = false;
    boolean started;
    int COLUMN_COUNT;
    int MAX_POSITIONS;
    int turn;
    boolean sameTurn;
    Column[] columns;
    ScreenManager manager;

    public Connect4(ScreenManager manager) {
    	this.manager = manager;
    	addKeyListener(manager);
    	parent = manager.getDisplay();
    	
        PlayerColors = new HashMap<>();
        PlayerColors.put("Red", Color.RED);
        PlayerColors.put("Blue", Color.BLUE);
        currentPlayer = "Red";
        turn = 1;
        COLUMN_COUNT = 7;
        
        topLabel = new JLabel("CONNECT 4");
        topLabel.setOpaque(true);
        topLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        topLabel.setBackground(Color.BLACK);
        topLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        topLabel.setHorizontalAlignment(JLabel.CENTER);
        topLabel.setForeground(Color.WHITE);
        
        
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        MAX_POSITIONS = 7;
        setFocusable(true);
        
        addAncestorListener(new main.AncestorAdapter() {
        	public void ancestorAdded(AncestorEvent event) {
        		requestFocus();
        		parent.setLayout(new BorderLayout());
                parent.add(topLabel, BorderLayout.NORTH);
                parent.revalidate(); parent.repaint();
				if (!started) {
					start();
				}
			}
			
			public void ancestorRemoved(AncestorEvent event) {
                parent.remove(topLabel);
                parent.revalidate(); parent.repaint();
			}
		});
        
        addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent event) {
        		Arrays.stream(columns).forEach(col -> col.setPreferredSize(new Dimension(getWidth() / (columns.length + 2), getHeight() - 25)));
        		revalidate(); repaint();
        	}
        });
    }
    
    public void start() {
    	this.removeAll();
    	win = false;
    	started = true;
    	turn = 1;
    	currentPlayer = "Red";
    	
    	logicBoard = new String[MAX_POSITIONS][COLUMN_COUNT];
        for (int row = 0; row < logicBoard.length; row++) {
        	for (int col = 0; col < logicBoard[row].length; col++) {
        		logicBoard[row][col] = " ";
        	}
        }
    	
    	columns = new Column[COLUMN_COUNT];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Column(i);
            columns[i].setPreferredSize(new Dimension(this.getWidth() / (columns.length + 2), this.getHeight() - 25));
            System.out.println("Width: " + this.getWidth() + " Height: " + this.getHeight() );
            columns[i].setBackground(Color.BLACK);
            this.add(columns[i]);
            revalidate();
            repaint();
        }
    }
    
    public void winDialog() {
      win = true;
      started = false;
  	  int response = JOptionPane.showConfirmDialog(null, currentPlayer + " Wins! Would You Like To Play Again??");
  	  if (response == 0) {
  		  start();
  	  }
    }

    public void switchPlayer() {
        if (currentPlayer.equals("Red")) {
            currentPlayer = "Blue";
        } else { currentPlayer = "Red"; }
    }

    	
    //GAME LOGIC
    
    public void checkBoard() {
        String current = " "; 
        for (int row = 0; row < logicBoard.length; row++) {
          for (int col = 0; col < logicBoard[row].length; col++) {
            current = logicBoard[row][col];
            if (current.equals(currentPlayer)) {
              if (checkWin(row, col)) {
            	  System.out.println("WE HAVE A WINNER!: " + currentPlayer);
            	  winDialog();
            	  } //If it finds a letter corresponding to the current player's letter, it checks if that letter is part of a solve
            }
          }
        }
      }

      public boolean checkWin(int row, int col) {
        int[] nextPos = {0, 0};

        for (int i = row; i <= row + 1; i++) { //Since the program checks from top right to bottom left, any position above the current position has already been checked. Therefore, we don't need to check those
          for(int j = col - 1; j <= col + 1; j++) {
            try {
              if (logicBoard[i][j].equals(logicBoard[row][col]) && (row != i || col != j)) { //if it finds an equal move and it is not in the same position
                nextPos[0] = i;
                nextPos[1] = j;
                if (checkLine(row, col, nextPos)) { return true; }
              }
            } catch (Exception e) { 
            	//System.out.println("WALL"); 
            }

          }
        } return false;
      }
      
      //Possibly the worst piece of code I have ever written
      public boolean checkLine(int row, int col, int[] nextPos) {
        boolean win = false; int m = -2;
        int streak = 1;
        if (col != nextPos[1]) { m = -(nextPos[0] - row) / (nextPos[1] - col); } //Since the positive row is downward, the y axis has to be inverted for slope formula to work
        System.out.println("m = " + m + " at " + row + " " + col);

        if (m == 0) { //Horizontal
          if (nextPos[1] == col + 1) { // right
            if (col + 3 > logicBoard[row].length - 1) { return false; } //if there is not enough space
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row][col + i])) {
                streak++;
              } else { break; }
            }
          } else { //left
            if (col - 3 < 0) { return false; }
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row][col - i])) {
                streak++;
              } else { break; }
            }
          }
        } else if (m == 1) { //bottom left
            if (col - 3 < 0 || row + 3 > logicBoard.length - 1) { return false; }
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row + i][col - i])) {
                streak++; 
              } else { break; }
            }
        } else if (m == -1) { //bottom right
            if (row + 3 > logicBoard.length - 1 || col + 3 > logicBoard[row].length - 1) { return false; }
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row + i][col + i])) {
                streak++; 
              } else { break; }
            }
        } else { //vertical
          if (nextPos[0] == row + 1) { //down
            if (row + 3 > logicBoard.length - 1) { return false; }
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row + i][col])) {
                streak++;
              } else { break; }
            }
          } else { //up
            if (row - 3 < 0) { return false; }
            for (int i = 1; i < 4; i++) {
              if (logicBoard[row][col].equals(logicBoard[row - i][col])) {
                streak++;
              } else { break; }
            }
          }
        }
        
        if (streak == 4) { win = true; } //if it finds a streak of 4, the person wins and it returns all the way back to the game call
        System.out.println("streak = " + streak);
        return win;
      }
      
      //GAME LOGIC




    class Column extends JPanel implements MouseListener {
        int position = 0;
        int columnNumber;
        String[] filledPositions;
        
        Column(int columnNumber) {
        	this.columnNumber = columnNumber;
            this.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            this.addMouseListener(this);
            filledPositions = new String[MAX_POSITIONS];
        }

        public int getMAX_POSITIONS() {
            return MAX_POSITIONS;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //Nada
        }

        @Override
        public void mousePressed(MouseEvent e) {
        	if (!win) {
        	turn++;
            filledPositions[position] = currentPlayer;
            logicBoard[position][columnNumber] = currentPlayer;
            this.repaint();
            position++;
            checkBoard();
            switchPlayer();
        	}
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //Nada
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setBackground(new Color(30, 30, 30));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setBackground(new Color(0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            for (int i = 0; i < filledPositions.length; i++) {
                if (filledPositions[i] == null) { break; }
                g2D.setPaint(PlayerColors.get(filledPositions[i]));
                g2D.fillOval(0, this.getHeight() - this.getHeight() / MAX_POSITIONS * (i + 1), this.getWidth(), this.getHeight() / MAX_POSITIONS);
            }

        }
    }
}