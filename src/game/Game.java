package game;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import PGNParser.PGNMove;
import PGNParser.PGNParser;

public class Game {

    public static final int RESULT_NOT_STARTED = -1;
    public static final int RESULT_IN_PROGRESS = 0;
    public static final int RESULT_WHITE_WIN = 1;
    public static final int RESULT_BLACK_WIN = 2;
    public static final int RESULT_DRAW = 3;
    public static final int RESULT_TERMINATED = 4;

    public static final int REASON_IN_PROGRESS = 0;
    public static final int REASON_CHECKMATE = 1;
    public static final int REASON_FLAGFALL = 2;
    public static final int REASON_WHITE_DRAW = 3;
    public static final int REASON_BLACK_DRAW = 4;
    public static final int REASON_STALEMATE = 5;
    public static final int REASON_DEAD_INSUFFICIENT_MATERIAL = 6;
    public static final int REASON_DEAD_NO_POSSIBLE_MATE = 7;
    public static final int REASON_REPETITION = 8;
    public static final int REASON_FIFTY_MOVE = 9;
    public static final int REASON_OTHER = 10;

    private ArrayList<Position> positions;

    private int currentPos;

    private ArrayList<BoardMoveListener> moveListeners;

    /**
     * <p>
     * The result of the game.
     * 
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
     * 
     * <ul>
     * <li>0 - In progress or N/A
     * <li>1 - Win by checkmate
     * <li>2 - Win by flagfall of opponent
     * <li>3 - White proposed draw
     * <li>4 - Black proposed draw
     * <li>5 - Stalemate
     * <li>6 - Dead game - insufficient material
     * <li>7 - Dead game - no possible checkmate
     * <li>8 - Repetition
     * <li>9 - Fifty-move rule
     * <li>10 - Other
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

    private boolean paused;

    private long pauseStart;

    private ScheduledExecutorService flagfallChecker;

    Runnable flagfall = new Runnable() {

        public void run() {

            Position a = getCurrentCountdownPos();
            long start = a.getSystemTimeStart();

            if (start <= 0)
                return;

            long current = System.currentTimeMillis();
            if ((current - start) > (a.isWhite() ? whiteTimer : blackTimer)) {

                markGameOver(isWhiteTurn(true) ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_FLAGFALL);

            }

        }

    };

    public boolean isPaused() {
        return paused;
    }

    public int getTimePerSide() {
        return timePerSide;
    }

    public int getTimePerMove() {
        return timePerMove;
    }

    public Position getLastPos() {
        return positions.get(positions.size() - 1);
    }

    public Position getActivePos() {
        return positions.get(currentPos);
    }

    public Position getPreviousPos() {
        if (currentPos == 0)
            return null;

        return positions.get(currentPos - 1);
    }

    public int getResult() {
        return result;
    }

    public int getResultReason() {
        return resultReason;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    /**
     * @param timePerSide The time, in seconds, each side has at the start of the
     *                    game.
     */
    public void setTimePerSide(int timePerSide) {
        this.timePerSide = timePerSide * 1000;
    }

    /**
     * @param timePerMove The time, in seconds, each side gets at the end of each of
     *                    their moves.
     */
    public void setTimePerMove(int timePerMove) {
        this.timePerMove = timePerMove * 1000;
    }

    /**
     * Initializes a new Game with no time control.
     */
    public Game() {
        this(-1, -1);
    }

    /**
     * Initializes a new Game with the specified time control.
     * 
     * @param timePerSide The amount of time, in seconds, each side gets at the
     *                    start.
     * @param timePerMove The amount of time, in seconds, each side gets added after
     *                    each move they make.
     */
    public Game(int timePerSide, int timePerMove) {

        positions = new ArrayList<Position>();
        moveListeners = new ArrayList<BoardMoveListener>();

        positions.add(new Position(this));
        currentPos = 0;

        result = RESULT_NOT_STARTED;
        resultReason = REASON_IN_PROGRESS;

        this.timePerSide = timePerSide * 1000;
        this.timePerMove = timePerMove * 1000;

        this.whiteTimer = this.timePerSide;
        this.blackTimer = this.timePerSide;

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

    public String exportPosition() throws Exception {

        return new PGNParser(this, null, true).outputPGN(false);

    }

    public void makeMove(Move m) {

        if (paused || result != RESULT_IN_PROGRESS) {
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

        positions.add(movePosition);
        setCurrentPos(positions.size() - 1);

        if (movePosition.isCheckMate()) {
            fireMoveMade();
            markGameOver(movePosition.isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_CHECKMATE);
            return;
        }

        if (movePosition.isInsufficientMaterial()) {
            fireMoveMade();
            markGameOver(RESULT_DRAW, REASON_DEAD_INSUFFICIENT_MATERIAL);
            return;
        }

        if (movePosition.getMove().getPromoteType() != '?')
            flipTimer(true, 0);

        fireMoveMade();

    }

    public void startGame() {

        if (paused)
            return;

        result = RESULT_IN_PROGRESS;

        whiteTimer = timePerSide;
        blackTimer = timePerSide;

        flipTimer(true, 0);
        if (timePerSide > 0) {

            flagfallChecker = Executors.newScheduledThreadPool(1);
            flagfallChecker.scheduleWithFixedDelay(flagfall, 10, 10, TimeUnit.MILLISECONDS);

        }

    }

    public void stopGame() {
        if (flagfallChecker != null) {

            flagfallChecker.shutdownNow();

        }

    }

    /**
     * Marks the game as drawn.
     * 
     * @param colorOfOfferer The color, {@code true} if white and {@code false} if
     *                       black, of the side who offered the draw.
     */
    public void drawGame(boolean colorOfOfferer) {

        markGameOver(RESULT_DRAW, colorOfOfferer ? REASON_WHITE_DRAW : REASON_BLACK_DRAW);

    }

    public void markGameOver(int result, int resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        flagfallChecker.shutdown();

        gameOver();

    }

    public void setPromo(char piece) {

        if (getActivePos().getMove() != null && getActivePos().getMove().getPromoteType() == '?') {
            getActivePos().setPromoType(piece, this);
            flipTimer(true, 0);
        }

        fireMoveMade();
    }

    public void setCurrentPos(int currentPos) {

        if (currentPos >= positions.size() || currentPos < 0)
            return;

        int old = this.currentPos;

        this.currentPos = currentPos;

        firePosChanged(old, currentPos);

    }

    public boolean isWhiteTurn(boolean overall) {

        return (overall ? getLastPos() : getActivePos()).isWhite();

    }

    public boolean isLastPosCountdown() {
        return (getLastPos().getMove() == null || getLastPos().getMove().getPromoteType() != '?');
    }

    public Position getCurrentCountdownPos() {

        return isLastPosCountdown() ? getLastPos()
                : getPreviousPos();

    }

    public long getCurrentTimerTime(boolean color) {

        if (timePerSide <= -1)
            return -1;

        Position p = getCurrentCountdownPos();
        long timer = color ? whiteTimer : blackTimer;

        if (p.isWhite() != color)
            return timer >= 0 ? timer : 0;
        else {
            if (timer <= 0)
                return 0;
            else {
                long time = timer
                        - (System.currentTimeMillis() - p.getSystemTimeStart());

                if (time <= 0)
                    return 0;

                return time;
            }
        }

    }

    public void gameOver() {

        if (result <= 0)
            return;

        flipTimer(true, 0);

        fireGameOver();

    }

    /**
     * Flips the timer to the color of {@link #getActivePos()}.
     * 
     * <p>
     * If {@code setTimer}
     * is {@code true}, the {@link Position#timerEnd} for the previous position will
     * be set,
     * and either {@link #whiteTimer} or {@link #blackTimer} will be set. Will also
     * reset the timer task to schedule flagfall.
     * 
     * <p>
     * If the game is over, ({@link #result} does not equal {@code 0},) and
     * {@code setTimer} is true, then the flagfall timer will be stopped and a new
     * one will not be started.
     * 
     * @param setTimer  Whether or not the timer should be set.
     * @param pauseTime The time the game was paused at, if resuming. Should be
     *                  {@code 0} otherwise.
     */
    public void flipTimer(boolean setTimer, long pauseTime) {

        if (paused || timePerSide <= -1
                || (pauseTime <= 0 && getLastPos().getSystemTimeStart() > -1 && result == RESULT_IN_PROGRESS))
            return;

        Position active = getLastPos();
        Position previous = positions.size() - 2 >= 0 ? positions.get(positions.size() - 2) : null;

        long currentTime = System.currentTimeMillis();
        if (previous != null && setTimer && pauseTime <= 0) {

            if (previous.isWhite())
                whiteTimer -= (currentTime - previous.getSystemTimeStart()) - (timePerMove);
            else
                blackTimer -= (currentTime - previous.getSystemTimeStart()) - (timePerMove);

            previous.setTimerEnd(previous.isWhite() ? whiteTimer : blackTimer);

        }

        if (pauseTime > 0)
            active.setSystemTimeStart(currentTime - (pauseTime - active.getSystemTimeStart()));
        else if (resultReason != REASON_FLAGFALL)
            active.setSystemTimeStart(currentTime);

        if (result > RESULT_IN_PROGRESS) {

            if (active.isWhite()) {

                whiteTimer -= (currentTime - active.getSystemTimeStart());
                active.setTimerEnd(whiteTimer);

            } else {

                blackTimer -= (currentTime - active.getSystemTimeStart());
                active.setTimerEnd(blackTimer);

            }

        }

        fireTimerChange();

    }

    public void pauseGame() {

        if (paused)
            return;

        paused = true;
        pauseStart = System.currentTimeMillis();

        firePauseGame();

    }

    public void resumeGame() {

        if (!paused)
            return;

        paused = false;

        flipTimer(true, pauseStart);

        pauseStart = 0;

        fireResumeGame();

    }

    public boolean canUndo() {

        return currentPos == positions.size() - 1 && currentPos != 0;

    }

    public void undoMove() {

        if (!canUndo())
            return;

        boolean isCountdown = isLastPosCountdown();

        Position redo = positions.get(currentPos);

        positions.remove(currentPos);

        positions.get(currentPos - 1).setRedo(redo);
        redo.setRedoPromote(redo.getMove().getPromoteType());

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromoType('?', this);

        setCurrentPos(positions.size() - 1);

        redo.setSystemTimeStart(-1);
        redo.setTimerEnd(-1);

        if (result > RESULT_IN_PROGRESS) {
            result = RESULT_IN_PROGRESS;
            resultReason = REASON_IN_PROGRESS;
        }

        if (isCountdown) {

            Position prev = getPreviousPos();
            if (getLastPos().isWhite()) {
                blackTimer = prev == null ? timePerSide : prev.getTimerEnd();
                whiteTimer -= timePerMove;
            } else {
                whiteTimer = prev == null ? timePerSide : prev.getTimerEnd();
                blackTimer -= timePerMove;
            }

            getLastPos().setSystemTimeStart(-1);

            flipTimer(false, 0);

        }

        fireUndoMove();

    }

    public boolean canRedo() {

        return currentPos == positions.size() - 1 && positions.get(currentPos).getRedo() != null;

    }

    public void redoMove() {

        Position redo = getLastPos().getRedo();

        if (!canRedo())
            return;

        positions.add(redo);
        ++currentPos;
        redo.setPromoType(redo.getRedoPromote(), this);
        --currentPos;
        setCurrentPos(positions.size() - 1);

        boolean redoTime = getPreviousPos().getTimerEnd() > 0;

        if (redoTime) {

            if (getPreviousPos().isWhite())
                whiteTimer = getPreviousPos().getTimerEnd();
            else
                blackTimer = getPreviousPos().getTimerEnd();

        }

        flipTimer(!redoTime, 0);
        fireMoveMade();
        fireRedoMove();

    }

    public void addMoveListener(BoardMoveListener listener) {
        moveListeners.add(listener);
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

    public void fireGameOver() {

        for (BoardMoveListener b : moveListeners) {

            b.gameOver();

        }

    }

    public void fireTimerChange() {

        for (BoardMoveListener b : moveListeners) {

            b.timerChange();

        }

    }

    public void firePauseGame() {

        for (BoardMoveListener b : moveListeners) {

            b.pauseGame();

        }

    }

    public void fireResumeGame() {

        for (BoardMoveListener b : moveListeners) {

            b.resumeGame();

        }

    }

}
