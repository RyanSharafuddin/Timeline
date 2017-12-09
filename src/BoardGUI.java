import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BoardGUI extends JPanel {
	public static final int MOVENUMPAD = 5;
	private static final int LENGTH = TimelineBoard.LENGTH;
	public final TimelineBoard b;
	private final SquareWrapper[][][][] field = new SquareWrapper[LENGTH][LENGTH][LENGTH][LENGTH];
	public LinkedList<Move> history = new LinkedList<Move>(); //NOT for game logic; just for writing down game once over
	
	
	public JLabel status;
	public boolean showValid; //Will it highlight squares of valid moves?
	public Mode mode = Mode.Select;
	public Piece piece; //only matters for moving the piece
	public String warm_name;
	public String cold_name;
	public String date;
	public String gameName;
	public BufferedWriter writer;
	
	public BoardGUI(TimelineBoard board, boolean show, JLabel status) {
		b = board;
		showValid = show;
		this.status = status;
		this.setLayout(new GridLayout(LENGTH,LENGTH));
		for (int w = 0; w < LENGTH; w++) {
			for (int v = 0; v < LENGTH; v++) {
				JPanel section = new JPanel();
				//do the stuff for section
				section.setLayout(new GridLayout(LENGTH,LENGTH));
				section.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				for (int y = 0; y < LENGTH; y++) {
					for (int x = 0; x < LENGTH; x++) {
						Coord coord = new Coord (v, w, x, y);
						field[v][w][x][y] = new SquareWrapper(b.square(coord), this);
						field[v][w][x][y].addActionListener(new ButtonListener(field[v][w][x][y]));
						section.add(field[v][w][x][y]);
					}
				}
				//add the section
				this.add(section);
			}
		}
		baseStatus();
		
	}
	
	public SquareWrapper wrapper(Coord c) {
		return field[c.getVal(0)][c.getVal(1)][c.getVal(2)][c.getVal(3)];
	}
	
	public static Color getColor(Piece p) {
		switch(p.id) {
		//warm
		case 0:
			return Color.red; //red rook
		case 1:
			return new Color(255,140,0); //orange rook
		case 2:
			return new Color(255, 105, 180); //pink bishop
		case 3:
			return Color.yellow;	//yellow bishop
			
		//cold
		case 4:
			return Color.BLUE; //blue rook
		case 5:
			return new Color(101, 67, 33); //brown rook
		case 6:
			return Color.green;	//green bishop
		case 7:
			return new Color(100,0,100); //purple bishop
		
		}
		return null;
	}
	
	public static String colorString(Color c) {
		if(c.equals(Color.RED))
			return "Red";
		if(c.equals(new Color(255,140,0)))
			return "Orange";
		if(c.equals(new Color(255, 105, 180)))
			return "Pink";
		if(c.equals(Color.yellow))
			return "Yellow";
		if(c.equals(Color.BLUE))
			return "Blue";
		if(c.equals(new Color(101, 67, 33)))
			return "Brown";
		if(c.equals(Color.green))
			return "Green";
		if(c.equals(new Color(100,0,100)))
			return "Purple";
		return null;	
	}
	
	
	
	/* Handles highlighting of all squares*/
	public void setHighlight() {
		for(int v = 0; v < LENGTH; v++) {
			for(int w = 0; w < LENGTH; w++) {
				for (int x = 0; x < LENGTH; x++) {
					for(int y = 0; y < LENGTH; y++) {
						Coord c = new Coord(v,w,x,y);
						SquareWrapper sWrapper = wrapper(c);
						if(sWrapper.s.getPiece() != null && sWrapper.s.getCaptured() != null) {
							//piece sitting on a captured piece, so set it to highlight of captured piece
							sWrapper.hiColor = getColor(sWrapper.s.getCaptured().piece);
							sWrapper.highlight = true;
						}
						else if(showValid && piece != null && piece.isValid(c) != null) {
							sWrapper.hiColor = getColor(piece);
							sWrapper.highlight = true;
						}
						else {
							sWrapper.highlight = false;
						}
					}
				}
			}
		}
	}
	
	
	public void writeAllMoves() {
		ListIterator<Move> moveIterator = history.listIterator(0);
		try {
			int moveNum = 1;
			while (moveIterator.hasNext()) {
				String moveNumString = "" + moveNum + ": ";
				int l = moveNumString.length();
				for(int i = 0; i < MOVENUMPAD - l; i++) {
					moveNumString = moveNumString + " ";
				}
				writer.write("\n" + moveNumString);
				writer.write(moveIterator.next().toString());
				moveNum += 1;
			}
			writer.flush();
		} catch(IOException ex) {
			System.out.println("Game log error!");
		}
	}
	
	public void deselectPiece() {
		mode = Mode.Select;
		piece = null;
		baseStatus();
	}
	
	public void baseStatus() {
		String t = (b.getWarmTurn()) ? "Warm. " : "Cold. "; 
		String turn = "Move: " + b.moveNum + " Turn: " + t;
		status.setText(turn);
	}
	
	public void selectedStatus() {
		String p = (piece instanceof Rook) ? " rook" : " bishop";
		String t = (b.getWarmTurn()) ? " Turn: Warm. " : " Turn: Cold. ";
		String display = "Move: " + b.moveNum + t + "Selected Piece: " + colorString(getColor(piece)) + p;
		status.setText(display);
	}
	
	
	/* Sets gameover status text and writes game to gameLog*/
	public void finish() {
		Result end;
		if(b.warmLost)
			end = Result.WARMLOST;
		else if(b.coldLost)
			end = Result.COLDLOST;
		else
			end = Result.TIE;
		writeAllMoves();
		
		if(end == Result.TIE) {
			status.setText("It's a draw!");
			try {
				writer.write("\nDraw: "+ warm_name + " (Warm) " + 
						cold_name + " (cold)");
				writer.write("\n" + "END");
				writer.flush();
				writer.close();
			}
			catch (IOException ex) {
				System.out.println("Game log error!");
			}
			return;
		}
		
		String winnerColor = (end == Result.WARMLOST) ? "Cold" : "warm";
		String winnerName = (end == Result.WARMLOST) ? cold_name: warm_name;
		String loserColor = (end == Result.WARMLOST) ? "Warm" : "Cold";
		String loserName = (end == Result.WARMLOST) ? warm_name : cold_name;
		String statusText = ((end == Result.WARMLOST) ? "Warm" : "Cold") + " got deaded.";
		status.setText(statusText);

		try {
			if (!gameName.equals("Demonstration")) {
				writer.write("\n" + "Winner: " + winnerName + " (" + winnerColor + ")");
				writer.write("\n" + "Loser: " + loserName + " (" + loserColor + ")");
				writer.write("\n" + "END");
			}
			writer.flush();
			writer.close();
		}
		catch (IOException ex) {
			System.out.println("Game log error!");
		}
	}
	/*
	 * If both sides only have opposite colored bishops which made all the captures.
	 */
	public boolean detectBishopTie() {
		List<Piece> aliveWarm = new LinkedList<Piece>();
		List<Piece> aliveCold = new LinkedList<Piece>();
		for (Piece p: b.warmPieces) {
			if(p.isOnBoard())
				aliveWarm.add(p);
		}
		if(aliveWarm.size() != 1 || !(aliveWarm.get(0) instanceof Bishop))
			return false;
		//warm has 1 piece and it's a bishop
		for (Piece p: b.coldPieces) {
			if(p.isOnBoard())
				aliveCold.add(p);
		}
		if(aliveCold.size() != 1 || !(aliveCold.get(0) instanceof Bishop))
			return false;
		//cold has 1 piece and it's a bishop
		
		if(wrapper(aliveWarm.get(0).getLocation()).color == wrapper(aliveCold.get(0).getLocation()).color)
			return false;
		//now, they only have opposite colored bishops. did they make all the captures?
		if(aliveWarm.get(0).numCaptures() != TimelineBoard.NUMPIECES-1 || aliveCold.get(0).numCaptures() != TimelineBoard.NUMPIECES-1)
			return false;
		
		return true;
	}
	
	/* buttonPressed in select mode for Coord c*/
	public void selectMode(Coord c) {
		if (this.b.square(c).getPiece() != null) {
			if (this.b.square(c).getPiece().isWarm == b.getWarmTurn()) {
				piece = b.square(c).getPiece();
				mode = Mode.Move;
				selectedStatus();
			}
			else {
				System.out.println(" \n Enemy team's piece");
			}
		}
		else {
			System.out.println("\n There's no piece here");
		}
	}
	
	public void moveMode(Coord c) {
		List<Coord> wouldAdd = piece.isValid(c);
		if (wouldAdd != null) {
			history.add(new Move(piece, new Coord(piece.getLocation()), c));
			boolean gameEnded = b.move(piece, wouldAdd);
			deselectPiece();
			if(gameEnded || b.getMoves().isEmpty()) {
				b.gameOver = true;
				finish();
			}
		}
		//if you clicked on another one of your own pieces, the piece to move should become that piece.
		else if (b.square(c).getPiece() != null) {
			if (b.square(c).getPiece().isWarm == piece.isWarm) {
				piece = b.square(c).getPiece();
				selectedStatus();
			}
			else {
				System.out.println("Can't capture this enemy");
				deselectPiece();
			}
		}
		else {
			System.out.println("This is an invalid move");
			deselectPiece();
		}
	}

	public void buttonPressed(Coord c) {
		
		if(b.gameOver)
			return;
		
		if(b instanceof TimelineBot && b.getWarmTurn() == ((TimelineBot) b).isWarm) {
			history.add(((TimelineBot) b).botMove());
			baseStatus();
			if(b.gameOver || b.getMoves().isEmpty()) {
				b.gameOver = true;
				finish();
			}
		}
		
		else if (mode == Mode.Select)
			selectMode(c);
		
		else if (mode == Mode.Move) 
			moveMode(c);
		
		setHighlight();
		
		if(detectBishopTie()) {
			System.out.println("There are only opposite colored bishops which cannot free anything.\n"
					+ "This game is a tie.");
			b.gameOver = true;
			finish();
		}
	}
	


}
