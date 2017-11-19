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


	public String toString() {
		String s = "\nUndoDetails string: \nMover is " + mover.name() + "\numMoved is ";
		s += numMoved + "\n the vanishedTimeline is: ";
		if(vanishedTimeline == null || vanishedTimeline.isEmpty()) {
			s += "null or empty";
		}
		else {
			for (Coord c: vanishedTimeline) {
				s += "\n" + c;
			}
		}

		s += (aliveAtEnd) ? "\nalive" : "\ndead";
		s += "Done printing UndoDetail\n";
		return s;

	}
}

