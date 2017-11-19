
public class Marker {
	public final Piece piece;
	public final int num;
	
	public Marker(Piece piece, int num) {
		this.num = num;
		this.piece = piece;
	}
	
	public String toString() {
		return "This marker is for piece " + piece.name() + " and has num: " + num;
	}

}
