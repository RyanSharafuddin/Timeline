import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//TODO: 
//		
//      test undo



public class TimelineBoard {
	public static final int LENGTH = 4;
	public static final boolean WARMFIRST = true;
	public static final int NUMPIECES = 4;
	
	private final TimelineSquare[][][][] field = new TimelineSquare[LENGTH][LENGTH][LENGTH][LENGTH];
	private boolean warmTurn = WARMFIRST;
	public boolean gameOver = false;
	boolean warmLost = false;
	boolean coldLost = false;
	
	public Piece[] warmPieces = new Piece[NUMPIECES];
	public Piece[] coldPieces = new Piece[NUMPIECES];
	
	public int moveNum = 1;
	private Stack<UndoDetails> history = new Stack<UndoDetails>();
	
	
	public TimelineBoard() {
		for (int w = 0; w < LENGTH; w++) {
			for (int v = 0; v < LENGTH; v++) {
				for (int y = 0; y < LENGTH; y++) {
					for (int x = 0; x < LENGTH; x++) {
						Coord location = new Coord (v, w, x, y);
						field[v][w][x][y] = new TimelineSquare(location, this);
					}
				}
			}
		}
		//The board has been made, now put the pieces on the board.
		new Rook(new Coord(0,0,0,0), true, 0, this);
		new Rook(new Coord(0,0,3,0), true, 1, this); //orange
		new Bishop(new Coord(0,0,1,0), true, 2, this); //pink
		new Bishop(new Coord(0,0,2,0), true, 3, this);
		
		
		new Rook(new Coord(3,3,0,3), false, 4, this);
		new Rook(new Coord(3,3,3,3), false,  5, this); //brown
		new Bishop(new Coord(3,3,1,3), false, 6, this);
		new Bishop(new Coord(3,3,2,3), false,  7, this); //purple
		
	}
	
	
	
	public TimelineSquare square(Coord c) {
		return field[c.getVal(0)][c.getVal(1)][c.getVal(2)][c.getVal(3)];
	}
	
	public boolean getWarmTurn() {
		return warmTurn;
	}
	
	
	public Result getResult() {
		if(!gameOver) {
			throw new RuntimeException("Asked for result, but game's not over!");
		}
		if(warmLost)
			return Result.WARMLOST;
		if(coldLost)
			return Result.COLDLOST;
		return Result.TIE;
	}
	
	public boolean checkSideLost(boolean sideToCheck) {
		Piece[] toCheck = (sideToCheck) ? warmPieces : coldPieces;
		for(Piece enemy: toCheck) {
			if (enemy.isOnBoard()) {
				return false;
			}
		}
		if(sideToCheck) {
			warmLost = true;
		}
		else {
			coldLost = true;
		}
		gameOver = true;
		return gameOver;
	}
	

	public List<AnalyticalMove> getMoves() {
		List<AnalyticalMove> moves = new ArrayList<AnalyticalMove>();
		Piece[] pieces = (warmTurn) ? warmPieces : coldPieces;
		for(Piece p: pieces) {
			if(p.isOnBoard()) {
				p.addMovesToList(moves);
			}
		}
		return moves;
	}
	
	
	/*
	 * given the square the capture took place, returns alive at end boolean and fills 
	 * the vanished timeline, and sets board appropriately
	 */
	public boolean captureAt(TimelineSquare lastSquare, List<Coord> vanishedTimeline) {
		boolean aliveAtEnd = true;
		Marker l = lastSquare.getMarker();
		Piece killed = l.piece;
		vanishedTimeline.addAll(killed.killAt(l.num));
		lastSquare.setCaptured(l);
		if(!(vanishedTimeline.isEmpty())) {
			int vanishedSize = vanishedTimeline.size();
			Coord vLast = vanishedTimeline.get(vanishedSize - 1);
			TimelineSquare vLSquare = square(vLast);
			aliveAtEnd = vLSquare.getMarker().piece.equals(killed);
			if(aliveAtEnd) {
				if(vLSquare.getCaptured() != null) {
					vLSquare.setMarker(vLSquare.getCaptured());
					vLSquare.setCaptured(null);
					vLSquare.setPiece(vLSquare.getMarker().piece);
					vLSquare.getMarker().piece.resurrect();
				}
				else {
					vLSquare.setMarker(null);
					vLSquare.setPiece(null);
				}
			}
			else {
				vLSquare.setCaptured(null);
			}
			//up to second to last
			for(int i = 0; i <= vanishedSize -2; i++) {
				Coord vanish = vanishedTimeline.get(i);
				TimelineSquare vSquare = square(vanish);
				if(vSquare.getCaptured() != null) {
					vSquare.setMarker(vSquare.getCaptured());
					vSquare.setCaptured(null);
					vSquare.setPiece(vSquare.getMarker().piece);
					vSquare.getMarker().piece.resurrect();
				}
				else {
					vSquare.setMarker(null);
				}
			}
		}
		return aliveAtEnd;
	}
	
	//returns true if that move ends the game OVERRIDE THAT FOR BOTS
	public boolean move(Piece p, List<Coord> wouldAdd) {
		square(p.getLocation()).setPiece(null);
		List<Coord> vanishedTimeline = new ArrayList<Coord>();
		boolean aliveAtEnd = true; //because is aliveatend if no vanished timeline
		
		int pTimeline = p.getTimelineLength();
		int wouldAddSize = wouldAdd.size();
		Coord last = wouldAdd.get(wouldAddSize - 1);
		TimelineSquare lastSquare = square(last);
		
		//up until second to last
		for(int i = 0; i <= wouldAddSize - 2; i++) {
			square(wouldAdd.get(i)).setMarker(new Marker(p, pTimeline + i));
		}
		
		Marker l = lastSquare.getMarker();
		if(l != null) {
			aliveAtEnd = captureAt(lastSquare, vanishedTimeline);
		}
		lastSquare.setMarker(new Marker(p, pTimeline + wouldAddSize -1));
		lastSquare.setPiece(p);
		p.advance(wouldAdd);
		history.push(new UndoDetails(p, wouldAddSize, vanishedTimeline, aliveAtEnd));
		moveNum += 1;
		warmTurn = !warmTurn;
		if(checkSideLost(warmTurn)) {	//sets warmLost and cold lost. Tie checking done by BoardGUI b/c too expensive otherwise
			return true;
		}
		return false;
	}
	
	
	public void unCaptureAt(TimelineSquare s, List<Coord> vt, boolean at) {
		//unkill
		Piece unkilled = s.getCaptured().piece;
		s.setMarker(s.getCaptured());
		s.setCaptured(null);
		int originalLength = unkilled.getTimelineLength();
		unkilled.unKillAt(vt, at);
		if(vt == null || vt.isEmpty()) {
			s.setPiece(unkilled);
		}
		else {
			s.setPiece(null);
			int vtSize = vt.size(); 
			Coord vtlc = vt.get(vtSize - 1); 
			TimelineSquare vtls = square(vtlc);
			if (at) {
				if(vtls.getPiece() != null) {
					vtls.getPiece().unResurrect();
				}
				vtls.setCaptured(vtls.getMarker());
				vtls.setMarker(new Marker(unkilled, unkilled.getTimelineLength() - 1)); //unkilled timeline length now updated
				vtls.setPiece(unkilled);
			}
			else {
				vtls.setCaptured(new Marker(unkilled, unkilled.getTimelineLength() - 1));
			}
			
			for(int i = 0; i <= vtSize -2; i++) {
				Coord vPath = vt.get(i);
				TimelineSquare sPath = square(vPath);
				if(sPath.getMarker() != null) {
					sPath.setCaptured(sPath.getMarker());
					sPath.getMarker().piece.unResurrect();
					sPath.setPiece(null);
				}
				sPath.setMarker(new Marker(unkilled, originalLength + i));
			}
		}
	}
	
	public void undo() {
		UndoDetails d = history.pop();
		Piece mover = d.mover;
		int length = d.numMoved;
		List<Coord> vt = d.vanishedTimeline;
		boolean at = d.aliveAtEnd;
		
		TimelineSquare current = square(mover.getLocation());
		
		if (current.getCaptured() != null) {
			unCaptureAt(current, vt, at);
		}
		else {
			current.setMarker(null);
			current.setPiece(null);
		}

		List<Coord> unmoved = mover.unAdvance(length);

		int x = 1;
		for (Coord c: unmoved) {
			if(x == 1) {
				x += 1;
				continue;	//skip first one b/c print in reverse order
			}
			square(c).setMarker(null);
		}
		square(mover.getLocation()).setPiece(mover);
		moveNum -= 1;
		warmTurn = !warmTurn;
		gameOver = warmLost = coldLost = false;
	}

}
