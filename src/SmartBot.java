import java.util.List;

//BE CAREFUL! IF BUGGY, MAY HAVE TO MAKE COPIES OF ANALYTICAL MOVES
//TO PREVENT INTERACTIONS WITH OTHER STUFF
public class SmartBot extends TimelineBot {
	public double eval;
	public int lookAhead = 5;
	AnalyticalMove bestMove;

	public SmartBot(boolean team) {
		super(team);
	}
	
	public int numRooks(boolean warm) {
		int count = 0;
		Piece[] pieces = (warm) ? warmPieces : coldPieces;
		if(pieces[0].isOnBoard())
			count += 1;
		if(pieces[1].isOnBoard())
			count += 1;
		return count;
	}
	
	public int numBishops(boolean warm) {
		int count = 0;
		Piece[] pieces = (warm) ? warmPieces : coldPieces;
		if(pieces[2].isOnBoard())
			count += 1;
		if(pieces[3].isOnBoard())
			count += 1;
		return count;
	}
	
	//WARM IS MAXIMIZER, COLD IS MINIMIZER!
	public double evaluate() {
		if (gameOver) {
			if(warmLost)
				return Double.NEGATIVE_INFINITY;
			if(coldLost)
				return Double.POSITIVE_INFINITY;
			return 0; //tie
		}
		double evaluation = 0;
		int warmRooks = numRooks(true);
		int coldRooks = numRooks(false);
		evaluation += (warmRooks - coldRooks) * 4;
		
		int warmBishops = numBishops(true);
		int coldBishops = numBishops(false);
		if (warmBishops == 2) 
			evaluation += 14;
		if (coldBishops == 2)
			evaluation += -14;
		if (warmBishops == 1)
			evaluation += 6;
		if (coldBishops == 1)
			evaluation += -6;
//		int warmTimeline = timelineSize(true, t);
//		int coldTimeline = timelineSize(false, t);
//		evaluation += (.1) * (warmTimeline - coldTimeline); //bonus to long timeline??
		return evaluation;
	}

	
	
	//the God algorithm. Warm is maximizer.
		public double alphaBeta(double alpha, double beta, boolean warm, int levelsToLook) {
			int depth = lookAhead - levelsToLook + 1;
			if ((gameOver) || (levelsToLook == 0)) {
				return evaluate();
			}
			List<AnalyticalMove> moves = getMoves();
			int numMoves = moves.size();
			if (numMoves == 0) {
				return 0; //tie
			}
			double score;
			if (levelsToLook == lookAhead) { //don't initialize to null.
				bestMove = moves.get(0);
			}
		
			if (warm) {
				for (AnalyticalMove m: moves) {
					move(m.p, m.toAdd);
					score = alphaBeta(alpha, beta, (!warm), levelsToLook - 1);
					undo();
					if (score > alpha) {
						alpha = score;
						if (levelsToLook == lookAhead) {
							bestMove = m;
						}
					}
					if (alpha >= beta) {
						return alpha;
					}
				}
				return alpha;
			}
			
			else { //minimizer (cold) turn
				for (AnalyticalMove m: moves) {
					move(m.p, m.toAdd);
					score = alphaBeta(alpha, beta, (!warm), levelsToLook - 1);
					undo();
					if (score < beta) {
						beta = score;
						if (levelsToLook == lookAhead) {
							bestMove = m;
						}
					}
					if (alpha >= beta) {
						return beta;
					}
				}
				return beta;
			}
		}
	
	
	
	
	@Override
	public AnalyticalMove chooseMove() {
		eval = alphaBeta(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, isWarm, lookAhead);
		return bestMove;
	}

}
