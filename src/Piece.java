import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public abstract class Piece {
	public final TimelineBoard b;
	public final boolean isWarm;
	public final int id;
	private final Coord location = new Coord(0,0,0,0);
	private boolean onBoard = true;
	private final List<Coord> timeline = new ArrayList<Coord>();
	
	public Piece(Coord l, boolean warm, int x, TimelineBoard tb) {
		isWarm = warm;
		b = tb;
		location.setCoord(l);
		id = x;
		
		timeline.add(new Coord(l));
		b.square(l).setPiece(this);
	
		
		b.square(l).setMarker(new Marker(this, 0));
		Piece[] teamArray = (warm) ? b.warmPieces : b.coldPieces;
		teamArray[x % TimelineBoard.LENGTH] = this;
	}

	public int getTimelineLength() {
		return timeline.size();
	}
	
	public int numCaptures() { //mainly for use in detecting bishop ties
		int captures = 0;
		for(Coord c: timeline) {
			if(b.square(c).getCaptured() != null) {
				captures += 1;
			}
		}
		return captures;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		if (!(o instanceof Piece))
			return false;
		Piece other = (Piece) o;
		return (id == other.id);
	}
	
	
	public boolean isOnBoard() {
		return onBoard;
	}
	

	public Coord getLocation() {
		return location;
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	//returns which coords moving piece to dest would add if the path
	//is free, or null if outside of move abilities of piece
	public abstract List<Coord> wouldAdd(Coord dest);
	
	//adds all valid moves to the list, with killer heuristic
	public abstract void addMovesToList(List<AnalyticalMove> moves);
	
	//returns whether moving there is valid given the board
	public List<Coord> isValid(Coord c) {
		List<Coord> toAdd = wouldAdd(c);
		if(toAdd == null) {
			return null;
		}
		int size = toAdd.size();
		if(size == 0)
			System.out.println("ERROR!!\n");
		
		for (int i = 0; i <= size - 2; i++) { //from the 1st(0) to second to last
			if (b.square(toAdd.get(i)).getMarker() != null) {
				return null;
			}
		}

		//the square it lands on
		if (b.square(toAdd.get(size-1)).getCaptured() != null) {
			return null;
		}
		
		Marker m = b.square(toAdd.get(size-1)).getMarker();

		if (m != null && m.piece.isWarm == isWarm) {
			return null;
		}
		return toAdd;
	}
	
	//These functions only take care of the pieces. You need to deal with board in board
	//////////////////REGULAR IN GAME MOVES///////////////////
	//moves it along by all the coords in toAdd, assuming it's alive currently
	public void advance(List<Coord> toAdd) {
		Coord last = toAdd.get(toAdd.size() - 1);
		location.setCoord(last);
		timeline.addAll(toAdd);
	}
	
	//kills the piece at num position on its timeline (the num of its marker)
	//returns the portion of the timeline that is destroyed
	public List<Coord> killAt(int num) {
		List<Coord> destroyed = new ArrayList<Coord>();
		onBoard = false;
		location.setCoord(new Coord(-1,-1,-1,-1));
		ListIterator<Coord> it = timeline.listIterator(num + 1);
		while(it.hasNext()) {
			destroyed.add(it.next());
			it.remove();
		}
		return destroyed;	
	}
	
	//only for resurrecting pieces in game (i.e. at end of timeline)
	public void resurrect() {
		onBoard = true;
		Coord last = timeline.get(timeline.size() - 1);
		location.setCoord(last);
	}
	
	//////////////////////////////////////////////////////////////////////////
	//The next 3 functions are reverses of above functions: they are for unmove
	public List<Coord> unAdvance(int goBackBy) {
		List<Coord> unmoved = new ArrayList<Coord>();
		ListIterator<Coord> it = timeline.listIterator(timeline.size());
		int x = goBackBy;
		while(x > 0) {
			unmoved.add(it.previous());
			it.remove();
			x -= 1;
		}
		location.setCoord(it.previous());
		return unmoved;
	}
	
	//undo the capture of this piece, add toAdd into its timeline
	//and given whether it's alive at end or not
	//if toAdd is empty, means its timeline has not gained anything
	public void unKillAt(List<Coord> toAdd, boolean aliveatEnd) {
		if(toAdd.isEmpty()) {
			resurrect();
			return;
		}
		Coord last = toAdd.get(toAdd.size() - 1);
		timeline.addAll(toAdd);
		onBoard = aliveatEnd;
		location.setCoord(aliveatEnd ? last : new Coord(-1,-1,-1,-1));
	}
	
	public void unResurrect() {
		onBoard = false;
		location.setCoord(-1,-1,-1,-1);
	}
	
	
	
	@Override
	public String toString() {
		String s = "\n";
		s += BoardGUI.colorString(BoardGUI.getColor(this));
		s += (this instanceof Rook) ? " rook" : " bishop"; //Only valid as long as there are only two pieces
		if (onBoard) {
			String l = location.toString();
			s += "\n Location: " + l;
		}
		else {
			s += " \nNot on board, but location is " + location;
		}
		String t = "\n Timeline:";
		for (Coord ti: timeline) {
			t += "\n" + ti.toString();
		}
		s += t;
		s += "done printing piece\n";
		return s;
	}
	
	public String name() {
		String p = (this instanceof Rook) ? " rook" : " bishop";
		String display = BoardGUI.colorString(BoardGUI.getColor(this)) + p;
		return display;
	}

}
