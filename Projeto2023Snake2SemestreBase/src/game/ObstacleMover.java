package game;

import environment.Board;
import environment.Cell;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class ObstacleMover extends Thread {
	private Obstacle obstacle;
	private Board board;
	private CyclicBarrier barrier; 

	public ObstacleMover(Obstacle obstacle, Board board, CyclicBarrier barrier) {
		super();
		this.obstacle = obstacle;
		this.board = board;
		this.barrier = barrier; 
	}

	@Override
	public void run() {
		try {
			while(obstacle.getRemainingMoves() > 0) {
				Thread.sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
				while(true){
					Cell nextCell = board.getCell(board.getRandomPosition());
					if(!nextCell.setGameElementObstacle(this.obstacle))
						continue;
					board.getCell(this.obstacle.getPos()).removeObstacle();
					this.obstacle.setPos(nextCell.getPosition());
					board.setChanged();
					obstacle.decrementRemainingMoves();
					break;
				}
			}
			
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException ignore) {}
	}
}
