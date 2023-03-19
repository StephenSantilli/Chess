package game;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import game.PGNParser.PGNMove;
import game.PGNParser.PGNParser;

public class Game {

    public static final String VERSION = Game.class.getPackage().getImplementationVersion() == null ? "DEV"
            : Game.class
                    .getPackage().getImplementationVersion();

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

    /**
     * The settings of the game.
     */
    private GameSettings settings;

    private Player white;

    private Player black;

    /**
     * A list of the positions in this game, in order.
     */
    private ArrayList<Position> positions;

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
     * <p>
     * The time, in milliseconds, white has left. Will be {@code -1} if no time
     * control is being used.
     * 
     * <p>
     * <b>Note:</b> If it is currently white's turn, this will not be updated until
     * the timer is flipped.
     */
    private long whiteTimer;

    /**
     * <p>
     * The time, in milliseconds, black has left. Will be {@code -1} if no time
     * control is being used.
     * 
     * <p>
     * <b>Note:</b> If it is currently black's turn, this will not be updated until
     * the timer is flipped.
     */
    private long blackTimer;

    /**
     * Whether or not the game is paused.
     */
    private boolean paused;

    /**
     * The system time the game was paused.
     */
    private long pauseStart;

    /**
     * The service that checks for flagfall in the background.
     */
    private ScheduledExecutorService flagfallChecker;

    /**
     * The flagfall checker task.
     */
    Runnable flagfall = () -> {

        Position a = getCurrentCountdownPos();
        long start = a.getSystemTimeStart();

        if (start <= 0)
            return;

        long current = System.currentTimeMillis();
        if ((current - start) > (a.isWhite() ? whiteTimer : blackTimer)) {

            markGameOver(isWhiteTurn() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_FLAGFALL);

        }

    };

    public GameSettings getSettings() {
        return settings;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getResult() {
        return result;
    }

    public int getResultReason() {
        return resultReason;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * @return the most recent position, the last position in the list of
     *         positions
     */
    public Position getLastPos() {
        return positions.get(positions.size() - 1);
    }

    /**
     * @return the second to last position in the list of positions.
     */
    public Position getPreviousPos() {

        if (positions.size() == 1)
            return null;

        return positions.get(positions.size() - 1);

    }

    public Player getPlayer(boolean white) {
        return white ? this.white : this.black;
    }

    /**
     * Initializes a new Game with the specified time control and marks if there
     * will be an opponent.
     * 
     * @param timePerSide The amount of time, in seconds, each side gets at the
     *                    start.
     * @param timePerMove The amount of time, in seconds, each side gets added after
     *                    each move they make.
     * @param isTwoPlayer Whether or not there will be an opponent.
     */
    public Game(String whiteName, String blackName, GameSettings settings) {

        this.white = new Player(this, true, whiteName);
        this.black = new Player(this, false, blackName);

        this.settings = settings;

        positions = new ArrayList<Position>();

        positions.add(new Position(this));

        result = RESULT_NOT_STARTED;
        resultReason = REASON_IN_PROGRESS;

        this.whiteTimer = settings.getTimePerSide();
        this.blackTimer = settings.getTimePerSide();

    }

    public void startGame() throws Exception {

        if (paused)
            return;

        result = RESULT_IN_PROGRESS;

        whiteTimer = settings.getTimePerSide();
        blackTimer = settings.getTimePerSide();

        flipTimer(true, 0);

        if (settings.getTimePerSide() > 0) {

            flagfallChecker = Executors.newScheduledThreadPool(1);
            flagfallChecker.scheduleWithFixedDelay(flagfall, 10, 10, TimeUnit.MILLISECONDS);

        }

    }

    void markGameOver(int result, int resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        flagfallChecker.shutdown();

        gameOver();

    }

    public void stopGame() {

        if (flagfallChecker != null)
            flagfallChecker.shutdownNow();

    }

    public void importPosition(PGNParser PGN) throws Exception {

        positions = new ArrayList<Position>();
        positions.add(new Position(this));

        ArrayList<PGNMove> moves = PGN.getParsedMoves();

        for (int i = 0; i < moves.size(); i++) {

            Move m = new Move(moves.get(i).getMoveText(), getLastPos(), getLastPos().isWhite());

            positions.add(new Position(getLastPos(), m, this, !getLastPos().isWhite(), true));

        }

        if (positions.size() == 1)
            throw new Exception("Position import failed.");

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    public String exportPosition() throws Exception {

        return new PGNParser(this, null, true).outputPGN(false);

    }

    void makeMove(Square origin, Square destination) throws Exception {

        if (paused || result != RESULT_IN_PROGRESS)
            throw new Exception("Game is paused.");

        if (result != RESULT_IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        if (getLastPos().getMove() != null && getLastPos().getMove().getPromoteType() == '?')
            throw new Exception("Awaiting promotion for the last move.");

        Move valid = null;
        for (int i = 0; valid == null && i < getLastPos().getMoves().size(); i++) {

            Move a = getLastPos().getMoves().get(i);

            if (a.getOrigin().equals(origin) && a.getDestination().equals(destination))
                valid = a;

        }

        if (valid == null)
            throw new Exception("Invalid move.");

        Position movePosition = new Position(getLastPos(), valid, this, !isWhiteTurn(), true);

        if (movePosition.isGivingCheck())
            throw new Exception("Cannot move into check.");

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            throw new Exception("Cannot capture a king.");

        positions.add(movePosition);

        if (movePosition.isCheckMate()) {
            white.fireBoardUpdate();
            black.fireBoardUpdate();
            markGameOver(movePosition.isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_CHECKMATE);
            return;
        }

        if (movePosition.isInsufficientMaterial()) {
            white.fireBoardUpdate();
            black.fireBoardUpdate();
            markGameOver(RESULT_DRAW, REASON_DEAD_INSUFFICIENT_MATERIAL);
            return;
        }

        if (movePosition.getMove().getPromoteType() != '?')
            flipTimer(true, 0);

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    void setPromo(char piece) throws Exception {

        if (getLastPos().getMove() != null && getLastPos().getMove().getPromoteType() == '?') {

            getLastPos().setPromote(piece, this);
            flipTimer(true, 0);

        } else
            throw new Exception("Cannot set promote type.");

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    boolean isWhiteTurn() {

        return (getLastPos().isWhite() && isCountdownWhite());

    }

    /**
     * @deprecated
     *             Use {@link #isCountdownWhite()} instead.
     * 
     * @return Whether or not the {@link Position} in {@link #positions} is the
     *         one
     *         currently counting down the timer. Will be {@code false} if waiting
     *         for
     *         promotion.
     */
    @Deprecated
    public boolean isLastPosCountdown() {
        return (getLastPos().getMove() == null || getLastPos().getMove().getPromoteType() != '?');
    }

    /**
     * @return Whether or not the timer is currently counting down for white.
     */
    public boolean isCountdownWhite() {
        return (getLastPos().getMove() == null
                || (getLastPos().getMove().getPromoteType() != '?' && !getLastPos().isWhite())
                || getLastPos().isWhite());
    }

    public Position getCurrentCountdownPos() {

        return isCountdownWhite() == getLastPos().isWhite() ? getLastPos()
                : getPreviousPos();

    }

    public long getTimerTime(boolean color) {

        if (settings.getTimePerSide() <= -1)
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

    private void gameOver() {

        if (result <= 0)
            return;

        stopGame();

        flipTimer(true, 0);

        white.fireGameOver();
        black.fireGameOver();

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
    private void flipTimer(boolean setTimer, long pauseTime) {

        if ((!isCountdownWhite() && !settings.isWhiteTimerManged())
                || (isCountdownWhite() && !settings.isBlackTimerManaged())
                || paused
                || settings.getTimePerSide() <= -1
                || (pauseTime <= 0 && getLastPos().getSystemTimeStart() > -1 && result == RESULT_IN_PROGRESS))
            return;

        Position active = getLastPos();
        Position previous = positions.size() - 2 >= 0 ? positions.get(positions.size() - 2) : null;

        long currentTime = System.currentTimeMillis();
        if (previous != null && setTimer && pauseTime <= 0) {

            if (previous.isWhite())
                whiteTimer -= (currentTime - previous.getSystemTimeStart()) - (settings.getTimePerMove());
            else
                blackTimer -= (currentTime - previous.getSystemTimeStart()) - (settings.getTimePerMove());

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

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    void pauseGame() throws Exception {

        if (paused)
            throw new Exception("Game is already paused.");

        paused = true;
        pauseStart = System.currentTimeMillis();

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    void resumeGame() throws Exception {

        if (!paused)
            throw new Exception("Game is not paused.");

        paused = false;

        flipTimer(true, pauseStart);

        pauseStart = 0;

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    void undoMove() throws Exception {

        if (!settings.canUndo())
            throw new Exception("Game settings do not allow undo/redo.");

        if (positions.size() <= 1)
            throw new Exception("No move to undo.");

        boolean isCountdown = isLastPosCountdown();

        Position redo = getLastPos();

        positions.remove(positions.size() - 1);

        getLastPos().setRedo(redo);
        redo.setRedoPromote(redo.getMove().getPromoteType());

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromote('?', this);

        redo.setSystemTimeStart(-1);
        redo.setTimerEnd(-1);

        if (result > RESULT_IN_PROGRESS) {
            result = RESULT_IN_PROGRESS;
            resultReason = REASON_IN_PROGRESS;
        }

        if (isCountdown) {

            Position prev = getPreviousPos();
            if (getLastPos().isWhite()) {
                blackTimer = prev == null ? settings.getTimePerSide() : prev.getTimerEnd();
                whiteTimer -= settings.getTimePerMove();
            } else {
                whiteTimer = prev == null ? settings.getTimePerSide() : prev.getTimerEnd();
                blackTimer -= settings.getTimePerMove();
            }

            getLastPos().setSystemTimeStart(-1);

            flipTimer(false, 0);

        }

        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

    void redoMove() throws Exception {

        if (!settings.canUndo())
            throw new Exception("Game settings do not allow undo/redo.");

        Position redo = getLastPos().getRedo();

        if (redo == null)
            throw new Exception("No move to redo.");

        positions.add(redo);

        redo.setPromote(redo.getRedoPromote(), this);

        boolean redoTime = getPreviousPos().getTimerEnd() > 0;

        if (redoTime) {

            if (getPreviousPos().isWhite())
                whiteTimer = getPreviousPos().getTimerEnd();
            else
                blackTimer = getPreviousPos().getTimerEnd();

        }

        flipTimer(!redoTime, 0);
        white.fireBoardUpdate();
        black.fireBoardUpdate();

    }

}
