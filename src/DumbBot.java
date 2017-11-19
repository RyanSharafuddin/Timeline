import java.util.List;
import java.util.Random;


public class DumbBot extends TimelineBot {

	public DumbBot(boolean team) {
		super(team);
	}

	@Override
	public AnalyticalMove chooseMove() {
		List<AnalyticalMove> moves = getMoves();
		int numMoves = moves.size();
		Random generator = new Random();
		int randomInt = generator.nextInt(numMoves);
		return moves.get(randomInt);
	}

}
