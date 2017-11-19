import java.util.List;


public class UndoDetails {
	final Piece mover;	//which piece
	final int numMoved;	//how far it moved by
	final List<Coord> vanishedTimeline;	//the timeline that was destroyed CAN BE NULL OR EMPTY
	final boolean aliveAtEnd;

	public UndoDetails(Piece m, int x, List<Coord> l, boolean a) {
		mover = m;
		numMoved = x;
		vanishedTimeline = l;
		aliveAtEnd = a;
	}
}
