
public class Move {
	public final static int NAMEPAD = 15;
	Piece p;
	Coord start;
	Coord end;
	
	public Move(Piece p, Coord start, Coord end) {
		this.p = p;
		this.start = start;
		this.end = end;
	}
	
	
	@Override
	public String toString(){
		String result;
		String type = (p instanceof Rook) ? " rook: " : " bishop: ";
		String pieceString = BoardGUI.colorString(BoardGUI.getColor(p)) + type;
		int l = pieceString.length();
		for(int pad = 0; pad < NAMEPAD -l; pad++) {
			pieceString = pieceString + " ";
		}
		result = pieceString + start.toString() + " " + end.toString();
		return result;
	}

}
