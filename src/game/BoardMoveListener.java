package game;

import java.util.EventListener;

public interface BoardMoveListener extends EventListener {
    
    public void moveMade();

    public void undoMove();

    public void resetMoves();

}
