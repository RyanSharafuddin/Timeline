import java.util.ArrayList;
import java.util.List;


public class Bishop extends Piece {

	public Bishop(Coord l, boolean warm, int x, TimelineBoard tb) {
		super(l, warm, x, tb);
	}

	@Override
	public List<Coord> wouldAdd(Coord dest) {
		Coord move = dest.minus(getLocation());
		if (move.zeros() != 2) {
			return null;
		}
		int[] result = move.bResult();
		if (Math.abs(result[1]) != Math.abs(result[3])) { //if not the same amount of movement in both dimensions
			return null;
		}
		List<Coord> toAdd = new ArrayList<Coord>();
		int m = Math.abs(result[1]); //positive number of squares moved
		
		int fd = (result[1] > 0) ? 1 : -1; //first direction
		int sd = (result[3] > 0) ? 1 : -1; //second direction
		
		for (int i = 1; i <= m; i++) {
			Coord add = new Coord(getLocation());
			add.incrementVal(result[0], i * fd);
			add.incrementVal(result[2], i * sd);
			toAdd.add(add);
		}
		return toAdd;
	}

	@Override
	public void addMovesToList(List<AnalyticalMove> moves) {
		for (int dim = 0; dim < 3; dim++) {
			for(int dim2 = dim + 1; dim2 < 4; dim2++) {
			
				for(int direction = -1; direction <= 1; direction += 2) {
					for(int direction2 = -1; direction2 <= 1; direction2 +=2) {
					
						for(int i = 1; i <= 3; i++) {
							int a = getLocation().getVal(dim) + i * direction; 
							int b = getLocation().getVal(dim2) + i * direction2;
							if(a > 3 || a < 0 || b > 3 || b < 0) {
								break;
							}
							Coord check = new Coord(getLocation());
							check.incrementVal(dim, direction * i);
							check.incrementVal(dim2, direction2 * i);
							List<Coord> wouldAdd = isValid(check);
							if(wouldAdd == null) {
								break;
							}
							if(this.b.square(check).getMarker() != null) {
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
	}

}
