
public abstract class TimelineBot extends TimelineBoard {
	public final boolean isWarm;
	
	public TimelineBot(boolean team) {
		super();
		isWarm = team;
	}

	public abstract AnalyticalMove chooseMove();
	
	public Move botMove() {
		System.out.println("\nProcessing . . .");
		long startTime = System.currentTimeMillis();
		AnalyticalMove toMove = chooseMove();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Time taken: " + elapsedTime + " milliseconds.");
		if(this instanceof SmartBot) {
			System.out.println("alphaBeta result is " + ((SmartBot) this).eval);
		}
		Coord start = new Coord(toMove.p.getLocation());
		move(toMove.p, toMove.toAdd);
		Coord destination = new Coord(toMove.p.getLocation());
		System.out.println("Bot moves from " + start + " to " + destination);
		return new Move(toMove.p, start, destination);
	}
}
