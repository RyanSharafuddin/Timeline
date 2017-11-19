import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class TimelineGame implements Runnable {
	public static BoardGUI tb;
	public static boolean showValid;
	public Opponent opp;
	public boolean botIsWarm;
	public static String gameLog = "Gamelog.txt";
	
	
	public TimelineGame(boolean show, Opponent o, boolean e) {
		showValid = show;
		opp = o;
		botIsWarm = e;
	}
	
	public static JButton buttonMaker(final JCheckBox highlight, final Opponent opp, final JCheckBox enemyColor) {
		String s = "";
		if (opp == Opponent.HUMAN) {
			s += "Play Human";
		}
		if (opp == Opponent.DUMB) {
			s += "Play DumbBot";
		}
		if (opp == Opponent.SMART) {
			s += "Play SmartBot";
		}
		final JButton button = new JButton(s);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new TimelineGame(highlight.isSelected(), opp, enemyColor.isSelected()));
				Window w = SwingUtilities.getWindowAncestor(button);
				if (w != null) {
					w.setVisible(false);
				}	
			}
		});
		return button;
	}
	
	@Override
	public void run() {
		final JFrame frame = new JFrame("Timeline");
		
		final JButton undo = new JButton("Undo");
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tb.history.isEmpty()) {
					System.out.println("Nothing in GUI history");
					return;
				}
				if(tb.b.gameOver) {
					System.out.println("Too late to undo. Game is over!");
					return;
				}
				tb.deselectPiece();
				tb.b.undo();
				tb.undoStatus();
				tb.setHighlight();
				tb.history.removeLast();
				
			}
		});
		frame.add(undo, BorderLayout.NORTH);
		

		// Status panel
		final JPanel status_panel = new JPanel();
		frame.add(status_panel, BorderLayout.SOUTH);
		final JLabel status = new 
				JLabel("Turn: Warm. ");
		status_panel.add(status);
		
		long time = System.currentTimeMillis();
		Date today = new Date(time);
		Scanner s = new Scanner(System.in);
		System.out.println("Enter game name: ");
		String gameName = s.nextLine();

		
		if (opp == Opponent.HUMAN) {
			tb = new BoardGUI(new TimelineBoard(), showValid, status);
			System.out.println("Enter warm player's name: ");
			String wname = s.nextLine();
			System.out.println("Enter cold player's name: ");
			String cname = s.nextLine();
			s.close();
			tb.warm_name = wname;
			tb.cold_name = cname;
		}
		
		if (opp == Opponent.DUMB) {
			tb = new BoardGUI(new DumbBot(botIsWarm), showValid, status);
			System.out.println("Enter your name: ");
			String humanName = s.nextLine();
			s.close();
			tb.warm_name = (botIsWarm) ? "DumbBot" : humanName;
			tb.cold_name = (botIsWarm) ? humanName : "DumbBot";
		}
		
		if (opp == Opponent.SMART) {
			tb = new BoardGUI(new SmartBot(botIsWarm), showValid, status);
			System.out.println("Enter your name: ");
			String humanName = s.nextLine();
			s.close();
			tb.warm_name = (botIsWarm) ? "SmartBot" : humanName;
			tb.cold_name = (botIsWarm) ? humanName : "SmartBot";
		}
	
		tb.date = today.toString();
		tb.gameName = gameName;
		frame.add(tb, BorderLayout.CENTER);
		//Put frame on screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		
		JCheckBox[] boxes = new JCheckBox[2];
		boxes[0] = new JCheckBox("Show valid moves?");
		boxes[1] = new JCheckBox("Opponent is warm-colored?");
		
		JButton[] buttons = new JButton[3];
		buttons[0] = buttonMaker(boxes[0], Opponent.HUMAN, boxes[1]);
		buttons[1] = buttonMaker(boxes[0], Opponent.DUMB, boxes[1]);
		buttons[2] = buttonMaker(boxes[0], Opponent.SMART, boxes[1]);
		
		JOptionPane.showOptionDialog(null, boxes, "Game Options",
				0, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
		
		try {
			FileWriter w = new FileWriter(gameLog, true); //true means do append, not overwrite
			tb.writer = new BufferedWriter(w);
			String used = (showValid) ? "(Used showValid)" : "(No showValid)";
			if (!tb.gameName.equals("Demonstration")) {
				tb.writer.write("\n" + "\n" + tb.warm_name + " vs. " + tb.cold_name + " " + used);
				tb.writer.write("\n" + tb.date);
				tb.writer.write("\n" + tb.gameName);
			}
		}
		catch (IOException ex) {
			System.out.println("Game log error!");
		}
	}

}
