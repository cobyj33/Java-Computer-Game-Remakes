package chess;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;

public class Piece { //change
	private BufferedImage image;
	private int row;
	private int col;
	private int player;
	private boolean occupied;
	
	protected Piece(int row, int col) {
		this.row = row;
		this.col = col;
		player = 0;
		occupied = false;
		image = null;
	}
	
	public boolean equals(Piece Piece) {
		if (this.row == Piece.row && this.col == Piece.col) {
			return true;
		} return false;
	}
	
	public boolean isEnemy(Piece p) {
		if (p.getPlayer() != player && p.getPlayer() != 0) {
			return true;
		} return false;
	}
	
	public boolean isAlly(Piece p) {
		if (p.getPlayer() == player) {
			return true;
		} return false;
	}
	
	public boolean isFree(Piece p) {
		if (p.getPlayer() == 0) { return true; }
		return false;
	}
	
	public int getRow() { return row; }
	public int getCol() { return col; }
	public void setRow(int row) { this.row = row; }
	public void setCol(int col) { this.col = col; }
	
	public Piece[] getPossibleMoves(Chess game) { return null; }
	public int getPlayer() { return player; };
	public BufferedImage getImage() { return image; }
	public void setPlayer(int player) { this.player = player; };
	public void setOccupied(boolean bool) { occupied = bool; }
	public boolean isOccupied() { return occupied; }
	public void setImage(BufferedImage image) { this.image = image; }
	public void print() { System.out.println("Row: " + row + " Col: " + col); }
}


class Pawn extends Piece   {
	boolean firstTurn = true;
	
	Pawn(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.PAWN, player));
	}
	
	public void print() { System.out.print("Pawn "); super.print(); }
	
	public Piece[] getPossibleMoves(Chess game) {
		Piece[][] board = game.getBoard();
		LinkedList<Piece> moves = new LinkedList<>();
		int forward = getRow() + (-1 * game.getDirection());
		boolean blocked = !isFree(board[forward][getCol()]);
		
		for (int col = getCol() - 1; col <= getCol() + 1; col += 2) { //diagonal attack
			try {
				if (isEnemy(board[forward][col])) {
					moves.add(board[forward][col]);
				}
			} catch (IndexOutOfBoundsException exec) {
				continue;
			}
		}
		
		try {
		if (!blocked) {
			moves.add(board[forward][getCol()]);
				if (firstTurn && isFree(board[forward + (-1 * game.getDirection())][getCol()])) {
					moves.add(board[forward + (-1 * game.getDirection())][getCol()]);
				}
			}
		} catch (IndexOutOfBoundsException exec) {
			exec.printStackTrace();
		}
		
		
		return moves.toArray(new Piece[moves.size()]);
	}
}

class Rook extends Piece   {
	boolean firstTurn = true;
	
	Rook(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.ROOK, player));
	}
	
	public Piece[] getPossibleMoves(Chess game) {
		LinkedList<Piece> moves = new LinkedList<>();
		Piece[][] board = game.getBoard();
		int row = getRow();
		int col = getCol();
		Piece current;
		int[] blocked = new int[4];
		int i = 1;
		
		while (Arrays.stream(blocked).sum() != 4) {
			if (blocked[0] != 1) { //top
				try {
					current = board[row - i][col];
					if (isAlly(current)) {
						blocked[0] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[0] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[0] = 1;
				}
			}
			
			if (blocked[1] != 1) { //right
				try {
					current = board[row][col + i];
					if (isAlly(current)) {
						blocked[1] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[1] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[1] = 1;
				}
			}
			
			if (blocked[2] != 1) { //bottom
				try {
					current = board[row + i][col];
					if (isAlly(current)) {
						blocked[2] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[2] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[2] = 1;
				}
			}
			
			if (blocked[3] != 1) { //left
				try {
					current = board[row][col - i];
					if (isAlly(current)) {
						blocked[3] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[3] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[3] = 1;
				}
			}
			
			i++;
		}
		return moves.toArray(new Piece[moves.size()]);
	}
	
}

class Knight extends Piece   {
	boolean firstTurn = true;
	
	Knight(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.KNIGHT, player));
	}
	
	public Piece[] getPossibleMoves(Chess game) {
		LinkedList<Piece> moves = new LinkedList<>();
		Piece[][] board = game.getBoard();
		int row = 0;
		for (int i = 1; i <= 2; i++) {
			int col = getCol();
			for (int j = 1; j <= 4; j++) {
				try {
					col = j % 2 == 0 ? col + j : col - j;
					switch (i) {
						case 1: row = getRow() - 2 + ((j - 1) / 2); break;
						case 2: row = getRow() + 2 - ((j - 1) / 2); break;
					}
					if (!isAlly(board[row][col])) {
						moves.add(board[row][col]);
					}
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}
		}
		
		return moves.toArray(new Piece[moves.size()]);
	}
	
}

class Bishop extends Piece   {
	boolean firstTurn = true;
	
	Bishop(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.BISHOP, player));
	}
	
	public Piece[] getPossibleMoves(Chess game) {
		LinkedList<Piece> moves = new LinkedList<>();
		Piece[][] board = game.getBoard();
		int row = getRow();
		int col = getCol();
		Piece current;
		int[] blocked = new int[4];
		int distance = 1;
		
		while (Arrays.stream(blocked).sum() != 4) {
			if (blocked[0] != 1) { //top right
				try {
					current = board[row - distance][col + distance];
					if (isAlly(current)) {
						blocked[0] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[0] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[0] = 1;
				}
			}
			
			if (blocked[1] != 1) { //top left
				try {
					current = board[row - distance][col - distance];
					if (isAlly(current)) {
						blocked[1] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[1] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[1] = 1;
				}
			}
			
			if (blocked[2] != 1) { //bottom right
				try {
					current = board[row + distance][col + distance];
					if (isAlly(current)) {
						blocked[2] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[2] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[2] = 1;
				}
			}
			
			if (blocked[3] != 1) { //bottom left
				try {
					current = board[row + distance][col - distance];
					if (isAlly(current)) {
						blocked[3] = 1;
					} else {
						if (isEnemy(current)) {
							blocked[3] = 1;
						}
						moves.add(current);
					}
				} catch (IndexOutOfBoundsException exec) {
					blocked[3] = 1;
				}
			}
			
			distance++;
		}
		return moves.toArray(new Piece[moves.size()]);
	}
	
}

class Queen extends Piece   {
	boolean firstTurn = true;
	
	Queen(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.QUEEN, player));
	}
	
	public Piece[] getPossibleMoves(Chess game) {	
		Piece[] straights = new Rook(getRow(), getCol(), getPlayer()).getPossibleMoves(game);
		Piece[] diagonals = new Bishop(getRow(), getCol(), getPlayer()).getPossibleMoves(game);
		Piece[] fin = new Piece[straights.length + diagonals.length];
		System.arraycopy(straights, 0, fin, 0, straights.length);
		System.arraycopy(diagonals, 0, fin, straights.length, diagonals.length);
		return fin;
	}
	
}

class King extends Piece   {
	boolean firstTurn = true;
	
	King(int row, int col, int player) {
		super(row, col);
		setPlayer(player);
		setOccupied(true);
		setImage(Images.getImage(Images.KING, player));
	}
	
	public Piece[] getPossibleMoves(Chess game) {
		LinkedList<Piece> moves = new LinkedList<>();
		Piece[][] board = game.getBoard();
		for (int row = getRow() - 1; row <= getRow() + 1; row++) {
			for (int col = getCol() - 1; col <= getCol() + 1; col++) {
				if (row == getRow() && col == getCol()) { continue; }
				try {
					if (!isAlly(board[row][col])) {
						moves.add(board[row][col]);
					}
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}
		}
		
		return moves.toArray(new Piece[moves.size()]);
	}
}