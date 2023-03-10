package game;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import PGNParser.PGNMove;
import PGNParser.PGNParser;

public class Game {

    private ArrayList<Position> positions;

    private int currentPos;

    private ArrayList<BoardMoveListener> moveListeners;

    /**
     * <p>
     * The result of the game.
     * <ul>
     * <li>0 - In progress
     * <li>1 - White win
     * <li>2 - Black win
     * <li>3 - Draw
     */
    private int result;

    /**
     * <p>
     * The reason for the result of the game.
     * <ul>
     * <li>0 - In progress or N/A
     * <li>1 - Win by checkmate
     * <li>2 - Win by flagfall of opponent
     * <li>3 - White proposed draw
     * <li>4 - Black proposed draw
     * <li>6 - Stalemate
     * <li>7 - Dead game - insufficient material
     * <li>8 - Dead game - no possible checkmate
     * <li>9 - Repetition
     * <li>9 - Fifty-move rule
     */
    private int resultReason;

    /**
     * The time, in milliseconds, each side has in total. Should be {@code -1} if no
     * time
     * control used.
     */
    private int timePerSide;

    /**
     * The time, in milliseconds, each side gains per move made. Should be
     * {@code -1} if
     * no time control used, but {@code 0} if no timer added per move.
     */
    private int timePerMove;

    /**
     * The time, in milliseconds, white has left. Should be {@code -1} if no time
     * control is being used.
     */
    private long whiteTimer;

    /**
     * The time, in milliseconds, black has left. Should be {@code -1} if no time
     * control is being used.
     */
    private long blackTimer;

    private Timer clockScheduler;

    public long getWhiteTime() {
        if (timePerSide <= -1)
            return -1;

        if (!isWhiteTurn(true))
            return whiteTimer;
        else {
            if (whiteTimer == 0)
                return 0;
            else
                return whiteTimer - (System.currentTimeMillis() - getActivePos().getSystemTimeStart());
        }
    }

    public long getBlackTime() {
        if (timePerSide <= -1)
            return -1;

        if (isWhiteTurn(true))
            return blackTimer;
        else {
            if (blackTimer == 0)
                return 0;
            else
                return blackTimer - (System.currentTimeMillis() - getActivePos().getSystemTimeStart());
        }
    }

    public Position getActivePos() {
        return positions.get(currentPos);
    }

    public Position getPreviousPos() {
        if (currentPos == 0)
            return null;

        return positions.get(currentPos - 1);
    }

    public Position getPreviousPosByColor(int pos, boolean white) {

        if (pos == 0 || white && pos < 3 || !white && pos < 4)
            return null;

        return positions.get(pos - 2);

    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public Game() {
        this(-1, -1);
    }

    public Game(int timePerSide, int timePerMove) {

        positions = new ArrayList<Position>();
        moveListeners = new ArrayList<BoardMoveListener>();
        positions.add(new Position(this));
        currentPos = 0;
        result = 0;
        resultReason = 0;

        this.timePerSide = timePerSide * 1000;
        this.timePerMove = timePerMove * 1000;

        this.whiteTimer = this.timePerSide;
        this.blackTimer = this.timePerSide;

        this.clockScheduler = new Timer("clockScheduler");

        flipTimer();

    }

    public void flipTimer() {

        if (timePerSide <= -1 || getActivePos().getSystemTimeStart() > -1) {
            return;
        }

        boolean white = isWhiteTurn(true);
        try {
            clockScheduler.cancel();
        } catch (Exception e) {

        }
        long currentTime = System.currentTimeMillis();
        if (getPreviousPos() != null) {
            if (getPreviousPos().isWhite()) {
                whiteTimer -= (currentTime - getPreviousPos().getSystemTimeStart()) + (timePerMove * 1000);
            } else {
                blackTimer -= (currentTime - getPreviousPos().getSystemTimeStart()) + (timePerMove * 1000);
            }
            getPreviousPos().setTimerEnd(white ? whiteTimer : blackTimer);
        }

        getActivePos().setSystemTimeStart(currentTime);
        clockScheduler = new Timer("clockScheduler");

        TimerTask flagfall = new TimerTask() {

            public void run() {

                getActivePos().setTimerEnd(0);
                if (isWhiteTurn(true))
                    whiteTimer = 0;
                else
                    blackTimer = 0;

                fireFlagfall(isWhiteTurn(true));
                result = isWhiteTurn(true) ? 2 : 1;
                resultReason = 2;

            }

        };

        clockScheduler.schedule(flagfall, white ? whiteTimer : blackTimer);
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

        if (result != 0) {
            return;
        }

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

        if (movePosition.isCheckMate()) {
            result = (movePosition.isWhite() ? 2 : 1);
            resultReason = 1;
        }

        positions.add(movePosition);
        setCurrentPos(positions.size() - 1);

        flipTimer();

        fireMoveMade();

    }

    public void setCurrentPos(int currentPos) {

        if (currentPos >= positions.size() || currentPos < 0)
            return;

        int old = this.currentPos;

        this.currentPos = currentPos;

        firePosChanged(old, currentPos);

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

        redo.setSystemTimeStart(-1);
        redo.setTimerEnd(-1);

/*         Position prev = getPreviousPosByColor(currentPos + 1, redo.isWhite());
        if (!redo.isWhite()) {

            if (prev == null) {
                whiteTimer = timePerSide;
            } else {
                whiteTimer = prev.getTimerEnd();
            }

        } else {

            if (prev == null) {
                blackTimer = timePerSide;
            } else {
                blackTimer = prev.getTimerEnd();
            }

        } */

        flipTimer();
        fireUndoMove();

    }

    public void redoMove() {

        Position redo = positions.get(positions.size() - 1).getRedo();

        if (!canRedo())
            return;

        positions.add(redo);
        currentPos++;
        redo.setPromoType(redo.getRedoPromote(), this);
        setCurrentPos(positions.size() - 1);

        flipTimer();
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

    public void fireFlagfall(boolean white) {

        for (BoardMoveListener b : moveListeners) {

            b.flagfall(white);

        }

    }

    public void fireTimerChange(boolean white) {

        for (BoardMoveListener b : moveListeners) {

            b.timerChange(white);

        }

    }

    public void addMoveListener(BoardMoveListener listener) {
        moveListeners.add(listener);
    }

}
