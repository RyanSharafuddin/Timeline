import java.awt.Color;


public class TimelineSquare {
	private final Coord location;
	public final TimelineBoard b;
	
	private Piece piece;
	private Marker marker;
	private Marker captured; //if there's a second marker here
	
	public TimelineSquare (Coord location, TimelineBoard board) {
		this.location = location;
		this.b = board;
	}
	
	public void setPiece(Piece p) {
		piece = p;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public Coord getLocation() {
		return location;
	}
	
	public void setMarker(Marker m) {
		marker = m;
	}
	
	public Marker getMarker() {
		return marker;
	}
	
	public void setCaptured(Marker c) {
		captured = c;
	}
	
	public Marker getCaptured() {
		return captured;
	}
	
	public String toString() {
		String s = "\nPrinting contents of square at: " + location.toString() + "\n";
		s += "Marker is " + marker;
		s += "\nCaptured is " + captured;
		s += "\nPiece is " + piece;
		return s;
	}
	
}
