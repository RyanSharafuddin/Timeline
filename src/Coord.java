
public class Coord {
	private int[] values = new int[4];
	static final int LENGTH = TimelineBoard.LENGTH;
	
	
	//constructor
	public Coord(int v, int w, int x, int y) {
		values[0] = v;
		values[1] = w;
		values[2] = x;
		values[3] = y;
	}
	
	//copy constructor
	public Coord(Coord original) {
		values[0] = original.getVal(0);
		values[1] = original.getVal(1);
		values[2] = original.getVal(2);
		values[3] = original.getVal(3);
	}
	
	public int getVal(int x) {
		return values[x];
	}
	
	public void setVal(int dim, int val) {
		values[dim] = val;
	}
	
	public void incrementVal(int dim, int val) {
		values[dim] += val;
	}
	
	public void setCoord(Coord o) {
		values[0] = o.getVal(0);
		values[1] = o.getVal(1);
		values[2] = o.getVal(2);
		values[3] = o.getVal(3);
	}
	
	public void setCoord(int v, int w, int y, int x) {
		values[0] = v;
		values[1] = w;
		values[2] = x;
		values[3] = y;
	}
	
	@Override
	public int hashCode() { 
		//since all your co-ordingtes are between 0 and 3, guaranteed no collisions
		return (values[3] + (LENGTH * values[2]) + (LENGTH * LENGTH * values[1]) + 
				(LENGTH * LENGTH * LENGTH * values[0]));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Coord))
			return false;
		Coord other = (Coord) o;
		return ( (values[0] == other.getVal(0)) && 
				(values[1] == other.getVal(1)) && 
				(values[2] == other.getVal(2)) && 
				(values[3] == other.getVal(3)));
	}
	
	public Coord minus(Coord o) {
		return new Coord(
				values[0] - o.getVal(0), 
				values[1] - o.getVal(1), 
				values[2] - o.getVal(2), 
				values[3] - o.getVal(3) );
	}
	
	public int zeros() {
		int numZeros = 0;
		if (values[0] == 0)
			numZeros += 1;
		if (values[1] == 0) 
			numZeros += 1;
		if (values[2] == 0)
			numZeros += 1;
		if (values[3] == 0) 
			numZeros += 1;
		return numZeros;
	}
	
	public int[] rResult() {
		if (values[0] != 0)
			return new int[] {0, values[0]};
		if (values[1] != 0) 
			return new int[] {1, values[1]};
		if (values[2] != 0)
			return new int[] {2, values[2]};
		if (values[3]!= 0) 
			return new int[] {3, values[3]};
		throw new RuntimeException("rResult used incorrectly!");
	}
	

	
	//Call this on a Coord with 2 zeros.
	public int[] bResult() {
		int[] result = new int[4];
		int dracula = 0;
		for (int i = 0; i < 4; i++) {
			if (values[i] != 0) {
				result[dracula] = i;
				result[dracula + 1] = values[i];
				dracula += 2;
			}
		}
		return result;	
	}
	
	//for debugging - prints out the coordinate like (v, w, x, y)
	@Override
	public String toString() {
		String coord = "(" + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3] + ")";
		return coord;
	}
	
	//reverse of toString
	public static Coord toCoord(String s) {
		int v = Character.getNumericValue((s.charAt(1)));
		int w = Character.getNumericValue((s.charAt(4)));
		int x = Character.getNumericValue((s.charAt(7)));
		int y = Character.getNumericValue((s.charAt(10)));
		return new Coord(v,w,x,y);
	}
	

}
