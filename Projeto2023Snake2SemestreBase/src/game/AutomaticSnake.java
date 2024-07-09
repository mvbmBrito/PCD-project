package game;

import environment.LocalBoard;
import environment.Cell;
import environment.Board;
import environment.BoardPosition;

import java.util.List;

public class AutomaticSnake extends Snake {
    public AutomaticSnake(int id, LocalBoard board) {
        super(id, board);
    }

    protected Cell getNextCell() {
        Cell head = getCells().getFirst();

        List<BoardPosition> neighbourPos = getBoard().getNeighboringPositions(head);
        List<Cell> freePositions = null;
        if(this.wasInterrupted){
             freePositions = neighbourPos.stream()
                            .map((bp) -> this.getBoard().getCell(bp))
                            .filter((c) -> (!this.getCells().contains(c)) && (!c.isOcupied()) )
                            .sorted(this::compare)
                            .toList();
        }else {
            freePositions = neighbourPos.stream()
                    .map((bp) -> getBoard().getCell(bp))
                    .filter((c) -> !getCells().contains(c))
                    .sorted(this::compare)
                    .toList();
        }
        
        
        for (Cell nextCell : freePositions) {
            if (nextCell.getGameElement() instanceof Killer) {
                this.interrupt();
                System.out.println("Snake " + getIdentification() + " encountered a Killer and died.");
                return null;
            }
        }

        this.wasInterrupted =false;
        return freePositions.isEmpty() ? null : freePositions.get(0);
    }

    @Override
    public void run() {
        doInitialPositioning();
        while(!getBoard().isGameOverV2() && !this.isInterrupted()) {
            try {
                Thread.sleep(Board.PLAYER_PLAY_INTERVAL);
                Cell toMove = getNextCell();
                if(toMove == null){
                    continue;
                }else {
                    this.move(toMove);
                }
            } catch (InterruptedException e) {}
        }
        System.out.println(Thread.currentThread() + " Class: Automatic Snake ended");
    }

    public int compare(Cell c1, Cell c2){
        BoardPosition goalPos = this.getBoard().getGoalPosition();
        return  (int) Math.signum(c1.getPosition().distanceTo(goalPos)  - c2.getPosition().distanceTo(goalPos));
    }
}
