package chess;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	private static BufferedImage pieces; 
	public static final int KING = 0, QUEEN = 1, BISHOP = 2, KNIGHT = 3, ROOK = 4, PAWN = 5;
	private static boolean initialized = false;
	
	public static void init() {
		if (!initialized) {
			initialized = true;
			File file = new File("res/chess/chess sprites.png");
			try {
				pieces = ImageIO.read(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static BufferedImage getImage(int pos, int player) {
		pos = pos + (6 * (player - 1));
		try {
		return pieces.getSubimage((pos % 6) * 200, (pos / 6) * 200, 200, 200);
		} catch (RasterFormatException e) { System.out.println("Failed"); }
		return null;
	}
}
