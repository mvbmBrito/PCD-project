package environment;

import java.io.Serializable;
import java.util.*;

import game.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static environment.LocalBoard.NUM_SIMULTANEOUS_MOVING_OBSTACLES;

public abstract class Board extends Observable {
    protected Cell[][] cells;
    private BoardPosition goalPosition;
    public static final long PLAYER_PLAY_INTERVAL = 100; //default: 100
    public static final long REMOTE_REFRESH_INTERVAL = 200; //default: 200
    public static final int NUM_COLUMNS = 7;
    public static final int NUM_ROWS = 7;
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    protected LinkedList<Snake> snakes = new LinkedList<Snake>();
    protected LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
    protected LinkedList<ObstacleMover> obstacleMovers = new LinkedList<>();
    protected boolean isFinished = false;
    public final ExecutorService pool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);
    private Goal goal;

    public Board() {
        cells = new Cell[NUM_COLUMNS][NUM_ROWS];
        for (int x = 0; x < NUM_COLUMNS; x++) {
            for (int y = 0; y < NUM_ROWS; y++) {
                cells[x][y] = new Cell(new BoardPosition(x, y));
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public LinkedList<ObstacleMover> getObstacleMovers() {
        return obstacleMovers;
    }

    public void setObstacleMovers(LinkedList<ObstacleMover> obstacleMovers) {
        this.obstacleMovers = obstacleMovers;
    }

    public void interruptAllSnakes() {
        for (Snake s : this.getSnakes()) {
            s.interrupt();
        }
    }

    public void interruptAllObs() {
        for (ObstacleMover obs : this.getObstacleMovers())
            obs.interrupt();
    }

    public boolean isOutOfBound(BoardPosition cell) {
        if (cell == null)
            throw new NullPointerException();
        return cell.x < 0 ||
                cell.x >= Board.NUM_COLUMNS ||
                cell.y < 0 ||
                cell.y >= Board.NUM_ROWS;
    }

    public Cell getCell(BoardPosition cellCoord) throws IllegalArgumentException {
        if (isOutOfBound(cellCoord)) {
            throw new IllegalArgumentException();
        }
        return cells[cellCoord.x][cellCoord.y];
    }

    public BoardPosition getRandomPosition() {
        return new BoardPosition((int) (Math.random() * NUM_ROWS), (int) (Math.random() * NUM_ROWS));
    }

    public BoardPosition getAlwaysSame() {
        //This method exists only for debuging and problem finding
        return new BoardPosition(3, 3);
    }

    public BoardPosition getGoalPosition() {
        return goalPosition;
    }

    public void setGoalPosition(BoardPosition goalPosition) {
        if (isOutOfBound(goalPosition)) {
            throw new IllegalArgumentException();
        }
        this.goalPosition = goalPosition;
    }

    public void addGameElement(GameElement gameElement) {
        boolean placed = false;
        while (!placed) {
            BoardPosition pos = getRandomPosition();
            if (!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
                getCell(pos).setGameElement(gameElement);

                if (gameElement instanceof Goal) {
                    setGoalPosition(pos);
                } else if (gameElement instanceof Obstacle) {
                    ((Obstacle) gameElement).setPos(pos);
                }

                placed = true;
            }
        }
    }

    public List<BoardPosition> getNeighboringPositions(Cell cell) {
        ArrayList<BoardPosition> possibleCells = new ArrayList<BoardPosition>();
        BoardPosition pos = cell.getPosition();
        if (pos.x > 0)
            possibleCells.add(pos.getCellLeft());
        if (pos.x < NUM_COLUMNS - 1)
            possibleCells.add(pos.getCellRight());
        if (pos.y > 0)
            possibleCells.add(pos.getCellAbove());
        if (pos.y < NUM_ROWS - 1)
            possibleCells.add(pos.getCellBelow());
        return possibleCells;
    }

    protected Goal addGoal() {
        Goal goal2 = new Goal(this);
        addGameElement(goal2);
        return goal2;
    }

    public LinkedList<Snake> getSnakes() {
        return snakes;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        notifyObservers();
    }

    public LinkedList<Obstacle> getObstacles() {
        return obstacles;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public boolean isGameOverV2() {
        return isFinished;
    }

    public void setGameOver() {
        isFinished = true;
    }

    public boolean getGameOver() {
        return isFinished;
    }

    public abstract void init();

    public abstract void handleKeyPress(int keyCode);

    public abstract void handleKeyRelease();

    public void addSnake(Snake snake) {
        snakes.add(snake);
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public void setSnakes(LinkedList<Snake> snakes) {
        this.snakes = snakes;
    }

    public void setObstacles(LinkedList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }
}
