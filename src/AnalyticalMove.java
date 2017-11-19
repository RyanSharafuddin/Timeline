import java.util.ArrayList;
import java.util.List;


public class AnalyticalMove {

	public Piece p;
	public List<Coord> toAdd;

	public AnalyticalMove(Piece pi, List<Coord> t) {
		p = pi;
		toAdd = t;
	}


	public String toString() {
		String s = p.name() + "  " + toAdd;
		return s;
	}
}
