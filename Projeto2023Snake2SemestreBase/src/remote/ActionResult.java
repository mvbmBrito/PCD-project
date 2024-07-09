package remote;

import java.io.Serializable;

public class ActionResult implements Serializable {
    private boolean wasSuccessful;
    private boolean gameEnded;

    public ActionResult(boolean wasSuccessful, boolean gameEnded) {
        this.wasSuccessful = wasSuccessful;
        this.gameEnded = gameEnded;
    }

    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    public boolean gameEnded() {
        return gameEnded;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "wasSuccessful=" + wasSuccessful +
                ", gameEnded=" + gameEnded +
                '}';
    }
}
