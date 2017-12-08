import java.text.NumberFormat;
import java.util.Locale;


public abstract class TimelineBot extends TimelineBoard {
	public final boolean isWarm;
	
	public TimelineBot(boolean team) {
		super();
		isWarm = team;
	}

	public abstract AnalyticalMove chooseMove();
	public abstract void printStats();
	
	public Move botMove() {
		System.out.println("\nProcessing . . .");
		long startTime = System.currentTimeMillis();
		AnalyticalMove toMove = chooseMove();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Time taken: " + elapsedTime / 1000.0 + " seconds.");
		printStats();
		Coord start = new Coord(toMove.p.getLocation());
		move(toMove.p, toMove.toAdd);
		Coord destination = new Coord(toMove.p.getLocation());
		System.out.println("Bot moves from " + start + " to " + destination);
		return new Move(toMove.p, start, destination);
	}
}
