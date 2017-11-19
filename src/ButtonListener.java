import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//improved timeline
public class ButtonListener implements ActionListener {
	private SquareWrapper square;
	private Coord location;
	
	public ButtonListener(SquareWrapper s) {
		square = s;
		location = new Coord(s.s.getLocation());
	}
	
	public void actionPerformed(ActionEvent e) {
		this.square.bg.buttonPressed(location);
	}

}
