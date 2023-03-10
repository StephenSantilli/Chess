package game;

import java.util.ArrayList;

import PGNParser.PGNMove;
import PGNParser.PGNParser;

public class Game {

    private ArrayList<Position> positions;

    private int currentPos;

    private ArrayList<BoardMoveListener> moveListeners;

    public void firePosChanged(int old, int curr) {

        for (BoardMoveListener b : moveListeners) {

            b.posChanged(old, curr);

        }

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

    public void fireRedoMove() {

        for (BoardMoveListener b : moveListeners) {

            b.redoMove();

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

        positions = new ArrayList<Position>();
        moveListeners = new ArrayList<BoardMoveListener>();
        positions.add(new Position(this));
        currentPos = 0;

    }

    public void setPromo(char piece) {

        if (getActivePos().getMove() != null && getActivePos().getMove().getPromoteType() == '?') {
            getActivePos().setPromoType(piece, this);
        }

        fireMoveMade();
    }

    public boolean isWhiteTurn(boolean overall) {

        return positions.get(overall ? positions.size() - 1 : currentPos).isWhite();

    }

    public void makeMove(Move m) {

        if (currentPos != positions.size() - 1)
            return;

        Position prev = positions.get(positions.size() - 1);

        if (prev.getMove() != null && prev.getMove().getPromoteType() == '?')
            return;

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
        setCurrentPos(positions.size() - 1);

        fireMoveMade();

    }

    public void setCurrentPos(int currentPos) {

        if (currentPos >= positions.size() || currentPos < 0)
            return;

        int old = this.currentPos;

        this.currentPos = currentPos;

        firePosChanged(old, currentPos);

    }

    public Position getNextPos(int currentPos) {

        if (currentPos == positions.size() - 1 && positions.get(currentPos).getRedo() != null)
            return positions.get(currentPos).getRedo();
        else if (currentPos < positions.size() - 1)
            return positions.get(currentPos + 1);
        else
            return null;

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
        redo.setRedoPromote(redo.getMove().getPromoteType());

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromoType('?', this);

        setCurrentPos(positions.size() - 1);

        fireUndoMove();

    }

    public void redoMove() {

        Position redo = positions.get(positions.size() - 1).getRedo();

        if (!canRedo())
            return;

        positions.add(redo);
        redo.setPromoType(redo.getRedoPromote(), this);
        setCurrentPos(positions.size() - 1);

        fireMoveMade();
        fireRedoMove();

    }

    public void importPosition(PGNParser PGN) throws Exception {

        positions = new ArrayList<Position>();
        positions.add(new Position(this));
        setCurrentPos(0);

        ArrayList<PGNMove> moves = PGN.getParsedMoves();

        for (int i = 0; i < moves.size(); i++) {

            Move m = new Move(moves.get(i).getMoveText(), getActivePos(), getActivePos().isWhite());

            positions.add(new Position(getActivePos(), m, this, !getActivePos().isWhite(), true));
            ++currentPos;

        }

        if (currentPos == 0)
            throw new Exception("Position import failed.");

        fireResetMoves();

    }

}
