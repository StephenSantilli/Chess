package game;

import java.util.EventListener;

public interface BoardMoveListener extends EventListener {
    
    public void moveMade();

    public void undoMove();

    public void redoMove();

    public void resetMoves();

    public void posChanged(int old, int curr);

}
