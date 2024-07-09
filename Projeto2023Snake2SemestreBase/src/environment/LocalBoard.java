package environment;

import game.AutomaticSnake;
import game.Killer;
import game.Obstacle;
import game.ObstacleMover;
import game.Snake;
import remote.ActionResult;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;

/** Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board {
	
	public static final int NUM_SNAKES = 2;	//default: 2
	private static final int NUM_OBSTACLES = 4;	//default: 25
	public static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3; //default: 3
	private CyclicBarrier barrier;

	public LocalBoard() {
	    barrier = new CyclicBarrier(NUM_SIMULTANEOUS_MOVING_OBSTACLES, new BarrierAction());
	    
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
			
		}
		addObstacles(NUM_OBSTACLES);
		addGoal();

		System.err.println("All elements placed");
	}

	public void init() {
		for(Snake s:snakes)
			s.start();
		setChanged();
	}
	
	public ActionResult processCoordinates(int x, int y) {
	    System.out.println("Processing coordinates: (" + x + ", " + y + ")");
	    Cell cell = getCell(new BoardPosition(x, y));
	    boolean successful = false;
	    boolean gameEnded = false;

	    
	    Obstacle obstacle = cell.getObstacle();
	    if (obstacle != null) {
	        System.out.println("Obstacle found and removed at: (" + x + ", " + y + ")");
	        cell.removeObstacle();
	        obstacles.remove(obstacle); 
	        successful = true;
	    }

	   
	    Snake occupyingSnake = cell.getOcuppyingSnake();
	    if (occupyingSnake != null && occupyingSnake.isInterrupted()) {
	        System.out.println("Dead snake found and removed at: (" + x + ", " + y + ")");
	        removeSnake(occupyingSnake); 
	        successful = true;
	    } else {
	       
	        for (Snake snake : snakes) {
	            if (snake.isInterrupted() && snake.getCells().contains(cell)) {
	                System.out.println("Part of dead snake found and removed at: (" + x + ", " + y + ")");
	                removeSnake(snake);
	                successful = true;
	                break;
	            }
	        }
	    }

	    gameEnded = checkGameEndConditions();

	    if (gameEnded) {
	        System.out.println("Game end conditions met. Ending game...");
	        setGameOver();  
	    } else {
	        System.out.println("Game continues.");
	    }

	    setChanged();
	    notifyObservers();  
	    
	    return new ActionResult(successful, gameEnded);
	}

	private void removeSnake(Snake snake) {
	    for (Cell cell : snake.getCells()) {
	        cell.release();
	    }
	    snakes.remove(snake);
	}

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}

	public boolean checkGameEndConditions() {
	    
	    boolean allSnakesDead = true;
	    for (Snake snake : this.snakes) {
	        if (!snake.isInterrupted()) {
	            allSnakesDead = false;
	            break;
	        }
	    }

	    
	    return allSnakesDead;
	}

	private class BarrierAction implements Runnable {
	    @Override
	    public void run() {
	        System.out.println("All ObstacleMovers have finished, adding a new Killer.");
	        addKiller();
	    }
	}
	
	private void addKiller() {
	    BoardPosition pos;
	    do {
	        pos = getRandomPosition();
	    } while (getCell(pos).isOcupied());

	    Killer killer = new Killer();
	    getCell(pos).setGameElement(killer);
	    System.out.println("Killer added at position: " + pos);
	}
	
	protected void addObstacles(int numberObstacles) {
	   
	    getObstacles().clear();
	    for (int i = 0; i < numberObstacles; i++) {
	        Obstacle obs = new Obstacle(this);
	        ObstacleMover lb = new ObstacleMover(obs, this, barrier); 
	        obstacleMovers.add(lb);

	        pool.submit(lb);

	        addGameElement(obs);
	        getObstacles().add(obs);
	    }
	}
}
