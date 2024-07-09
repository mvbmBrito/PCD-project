package game;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;

import java.io.Serializable;

public class Obstacle extends GameElement implements Serializable {
	
	private static final int NUM_MOVES=3; //default: 3
	public static final int OBSTACLE_MOVE_INTERVAL = 400; //default: 400
	private int remainingMoves=NUM_MOVES;
	private final transient Board board;

	private BoardPosition pos;

	public Obstacle(Board board) {
		super();
		this.board = board;

	}
	public BoardPosition getPos() {
		return pos;
	}

	public void setPos(BoardPosition pos) {
		this.pos = pos;
	}

//	public void move(Cell nextCell){
//		board.getCell(this.getPos()).removeObstacle();
//		nextCell.setGameElement(this);
//		this.setPos(nextCell.getPosition());
//		board.setChanged();
//	}

	public int getRemainingMoves() {
		return remainingMoves;
	}
	public void decrementRemainingMoves(){
		 remainingMoves--;
	}
}