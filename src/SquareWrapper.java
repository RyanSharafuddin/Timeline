import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;


public class SquareWrapper extends JButton {
	public TimelineSquare s;
	public BoardGUI bg;
	public final Color color;
	public boolean highlight; //if highlight, highlight in hiColor to show valid moves
	public boolean captured_highlight; 
	public Color hiColor;
	public final static int sideLength = 50;
	
	public SquareWrapper(TimelineSquare sq, BoardGUI b) {
		bg = b;
		s = sq;
		boolean black = true;
		for(int dim = 0; dim < 4; dim++) {
			if(s.getLocation().getVal(dim) % 2 == 0) {
				black = !black;
			}
		}
		color = (black) ? Color.black : Color.WHITE;
	}
	
	@Override
	public void paintComponent(Graphics gc) {
		Font f = new Font("F", Font.PLAIN, 20);
		gc.setFont(f);
		if (highlight) { //for showing valid moves.
			setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, hiColor));
		}
		else {
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		gc.setColor(color);
		gc.fillRect(0, 0, sideLength, sideLength);
		//If there's a piece, draw it
		Piece piece = s.getPiece();
		Marker captured = s.getCaptured();
		Marker marker = s.getMarker();
		if (piece != null) {
			gc.setColor(BoardGUI.getColor(piece));
			if (piece instanceof Rook) {
				gc.fillRect(5, 5, 40, 40);
			}
			if (piece instanceof Bishop) {
				gc.fillPolygon(new int[] {5, 25, 45}, new int[] {45, 5, 45}, 3);  //Was 0,25,50 and 50,0,50
			}
		}
		//No piece, but two markers
		else if (captured != null) { //should have else
			gc.setColor(BoardGUI.getColor(captured.piece));
			gc.fillOval(20, 20, 30, 30);
			gc.setColor(BoardGUI.getColor(marker.piece)); 
			gc.fillOval(0, 0, 30, 30);
			gc.setColor(( (marker.piece.isOnBoard()) ? Color.BLACK : Color.WHITE)); //marker might be captured in the future
			gc.drawString("" + marker.num, 4, 18); 
			gc.setColor(Color.WHITE); //draw captured number in white
			gc.drawString("" + captured.num, 23, 43);
		}
		//Just a marker and nothing else
		else if (marker != null) {
			gc.setColor(BoardGUI.getColor(marker.piece));
			gc.fillOval(7, 7, 36, 36);
			gc.setColor(( (marker.piece.isOnBoard()) ? (Color.BLACK) : (Color.WHITE))); //draw captured number in white
			//if number has 2 digits, move it a little to the left.
			gc.drawString("" + marker.num,( (marker.num >= 10) ? 15 : 20), 30);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sideLength,sideLength);
	}

}
