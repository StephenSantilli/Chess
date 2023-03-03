package game;

import java.util.ArrayList;

import PGNParser.PGNMove;
import PGNParser.PGNParser;

public class Game {

    private ArrayList<Position> positions;

    private int currentPos;

    private ArrayList<BoardListener> listeners;
    private ArrayList<BoardMoveListener> moveListeners;

    public void fireBoardUpdate() {

        for (BoardListener b : listeners) {

            b.boardUpdated();

        }

    }

    public char firePromptForPromote(Move move) {

        for (BoardListener b : listeners) {

            char c = b.promptForPromote(move);

            if (c == 'Q' || c == 'N' || c == 'B' || c == 'R')
                return c;

        }

        return '?';

    }

    public void addListener(BoardListener listener) {
        listeners.add(listener);
    }


    public void fireMoveMade() {

        for (BoardMoveListener b : moveListeners) {

            b.moveMade();

        }

    }

    public void fireUndoMove() {

        for (BoardMoveListener b : moveListeners) {

            b.undoMove();

        }

    }

    public void fireResetMoves() {

        for (BoardMoveListener b : moveListeners) {

            b.resetMoves();

        }

    }

    public void addMoveListener(BoardMoveListener listener) {
        moveListeners.add(listener);
    }

    public Position getActivePos() {
        return positions.get(currentPos);
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Game() {

        listeners = new ArrayList<BoardListener>();
        positions = new ArrayList<Position>();
        moveListeners = new ArrayList<BoardMoveListener>();
        positions.add(new Position(this));
        currentPos = 0;

    }

    public boolean isWhiteTurn(boolean overall) {

        return positions.get(overall ? positions.size() - 1 : currentPos).isWhite();

    }

    public void makeMove(Move m) {
        
        
        if (currentPos != positions.size() - 1)
            return;

        Position prev = positions.get(positions.size() - 1);

        if (prev.isCheckMate() || m.isWhite() != isWhiteTurn(true))
            return;

        Move valid = null;
        for (int i = 0; i < prev.getMoves().size(); i++) {
            Move a = prev.getMoves().get(i);

            if (a.equals(m))
                valid = a;
        }

        if (valid == null)
            return;

        Position movePosition = new Position(positions.get(currentPos), valid, this, !isWhiteTurn(true), true);

        if (movePosition.isGivingCheck())
            return;

        if (movePosition.isGivingCheck() && movePosition.isInCheck())
            return;

        if (movePosition.isGivingCheck() && !prev.isInCheck())
            return;

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            return;

        

        positions.add(movePosition);
        currentPos = positions.size() - 1;  

        fireBoardUpdate();
        fireMoveMade();

    }

    public void setCurrentPos(int currentPos) {

        if (currentPos >= positions.size() || currentPos < 0)
            return;

        this.currentPos = currentPos;

        fireBoardUpdate();

    }

    public boolean canUndo() {

        return currentPos == positions.size() - 1 && currentPos != 0;

    }

    public boolean canRedo() {

        return currentPos == positions.size() - 1 && positions.get(currentPos).getRedo() != null;

    }

    public void undoMove() {

        if (!canUndo())
            return;

        Position redo = positions.get(currentPos);

        positions.remove(currentPos);

        positions.get(currentPos - 1).setRedo(redo);

        if (redo.getMove().getPromoteType() != '0')
            redo.getMove().setPromoteType('?');

        currentPos = positions.size() - 1;

        fireBoardUpdate();
        fireUndoMove();

    }

    public void redoMove() {

        Position redo = positions.get(positions.size() - 1).getRedo();

        if (!canRedo())
            return;

        positions.add(redo);
        currentPos = positions.size() - 1;

        fireBoardUpdate();
        fireMoveMade();

    }

    public void importPosition(PGNParser PGN) throws Exception {

        positions = new ArrayList<Position>();
        positions.add(new Position(this));
        currentPos = 0;

        ArrayList<PGNMove> moves = PGN.getParsedMoves();

        for (int i = 0; i < moves.size(); i++) {

            Move m = new Move(moves.get(i).getMoveText(), getActivePos(), getActivePos().isWhite());

            positions.add(new Position(getActivePos(), m, this, !getActivePos().isWhite(), true));
            ++currentPos;
        }
        
        if (currentPos == 0)
        throw new Exception("Position import failed.");
        
        fireBoardUpdate();
        fireResetMoves();

    }

}
