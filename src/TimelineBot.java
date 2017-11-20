import java.text.NumberFormat;
import java.util.Locale;


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
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		System.out.println("Time taken: " + elapsedTime / 1000.0 + " seconds.");
		if(this instanceof SmartBot) {
			//String posString =  "" + ((SmartBot) this).positionsAnalyzed;
			String posString = numberFormat.format(((SmartBot) this).positionsAnalyzed);
			System.out.println("Positions evaluated: " + posString);
			System.out.println("alphaBeta result is " + ((SmartBot) this).eval);
		}
		Coord start = new Coord(toMove.p.getLocation());
		move(toMove.p, toMove.toAdd);
		Coord destination = new Coord(toMove.p.getLocation());
		System.out.println("Bot moves from " + start + " to " + destination);
		return new Move(toMove.p, start, destination);
	}
}
