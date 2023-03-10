package game;

import java.util.EventListener;

public interface BoardMoveListener extends EventListener {
    
    public void moveMade();

    public void undoMove();

    public void redoMove();

    public void resetMoves();

    public void posChanged(int old, int curr);

    /**
     * Event to signal the timer has switched sides.
     * @param white The side the clock is ticking down on.
     */
    public void timerChange(boolean white);

    public void flagfall(boolean white);

}
