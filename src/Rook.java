import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Rook extends Piece {

	
	public Rook(Coord l, boolean warm, int x, TimelineBoard tb) {
		super(l, warm, x, tb);
	}

	@Override
	public List<Coord> wouldAdd(Coord dest) {
		Coord move = dest.minus(getLocation());
		if (move.zeros() != 3) {
			return null;
		}
		List<Coord> toAdd = new ArrayList<Coord>();
		int[] result = move.rResult();
		int m = result[1];
		int direction = (m > 0) ? 1 : -1;

		for (int i = 1; i <= Math.abs(m); i++) {
			Coord add = new Coord(getLocation());
			add.incrementVal(result[0], direction * i);
			toAdd.add(add);
		}
		return toAdd;
	}

	@Override
	public void addMovesToList(List<AnalyticalMove> moves) {
		for (int dim = 0; dim < 4; dim++) {
			for(int direction = -1; direction <= 1; direction += 2) {
				for(int i = 1; i <= 3; i++) {
					int x = getLocation().getVal(dim) + i * direction; 
					if(x > 3 || x < 0) {
						break;
					}
					Coord check = new Coord(getLocation());
					check.incrementVal(dim, direction * i);
					List<Coord> wouldAdd = isValid(check);
					if(wouldAdd == null) {
						break;
					}
					if(b.square(check).getMarker() != null) {
						moves.add(0, new AnalyticalMove(this, wouldAdd)); //KILLER HEURISTIC
					}
					else {
						moves.add(new AnalyticalMove(this, wouldAdd));
					}
				}
			}		
		}
	}

}
