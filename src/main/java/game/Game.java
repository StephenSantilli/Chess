package game;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.PGN.PGNParser;

public class Game {

    public static final String VERSION = Game.class.getPackage().getImplementationVersion() != null
            ? Game.class.getPackage().getImplementationVersion()
            : "DEV";

    public static final int RESULT_NOT_STARTED = -1;
    public static final int RESULT_IN_PROGRESS = 0;
    public static final int RESULT_WHITE_WIN = 1;
    public static final int RESULT_BLACK_WIN = 2;
    public static final int RESULT_DRAW = 3;
    public static final int RESULT_TERMINATED = 4;

    public static final int REASON_IN_PROGRESS = 0;
    public static final int REASON_CHECKMATE = 1;
    public static final int REASON_FLAGFALL = 2;
    public static final int REASON_WHITE_OFFERED_DRAW = 3;
    public static final int REASON_BLACK_OFFERED_DRAW = 4;
    public static final int REASON_STALEMATE = 5;
    public static final int REASON_DEAD_INSUFFICIENT_MATERIAL = 6;
    public static final int REASON_DEAD_NO_POSSIBLE_MATE = 7;
    public static final int REASON_REPETITION = 8;
    public static final int REASON_FIFTY_MOVE = 9;
    public static final int REASON_RESIGNATION = 10;
    public static final int REASON_OTHER = 11;

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

    private ArrayList<GameListener> listeners;

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

        if (getTimerTime(true) <= 0)
            markGameOver(RESULT_BLACK_WIN, REASON_FLAGFALL);

        if (getTimerTime(false) <= 0)
            markGameOver(RESULT_WHITE_WIN, REASON_FLAGFALL);

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

        return positions.get(positions.size() - 2);

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

        this.white = new Player(whiteName);
        this.black = new Player(blackName);

        this.settings = settings;

        positions = new ArrayList<Position>();

        positions.add(new Position(this));

        result = RESULT_NOT_STARTED;
        resultReason = REASON_IN_PROGRESS;

        this.whiteTimer = settings.getTimePerSide();
        this.blackTimer = settings.getTimePerSide();

        this.listeners = new ArrayList<GameListener>();

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

        fireEvent(GameEvent.STARTED);

    }

    public boolean canDrawOffer() {

        return result == RESULT_IN_PROGRESS && getLastPos().getDrawOfferer() == Position.NO_OFFER;

    }

    public void acceptDrawOffer() throws Exception {

        if (result != RESULT_IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        if (getLastPos().getDrawOfferer() == Position.NO_OFFER)
            throw new Exception("No draw offer.");

        if (!canDrawOffer())
            throw new Exception("Cannot accept draw.");

        markGameOver(RESULT_DRAW,
                (getLastPos().getDrawOfferer() == Position.WHITE)
                        ? REASON_WHITE_OFFERED_DRAW
                        : REASON_BLACK_OFFERED_DRAW);

    }

    public void sendDrawOffer(boolean offererWhite) throws Exception {

        if (!canDrawOffer())
            throw new Exception("Cannot offer a draw.");

        getLastPos().setDrawOfferer(offererWhite ? Position.WHITE : Position.BLACK);
        fireEvent(GameEvent.DRAW_OFFER);

    }

    public void markGameOver(int result, int resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        if (result <= RESULT_IN_PROGRESS)
            return;

        if (flagfallChecker != null)
            flagfallChecker.shutdownNow();

        flipTimer(true, 0);

        fireEvent(GameEvent.OVER);

    }

    public void makeMove(Square origin, Square destination, char promoteType) throws Exception {

        if (paused)
            throw new Exception("Game is paused.");

        if (result != RESULT_IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        Move valid = null;
        for (int i = 0; valid == null && i < getLastPos().getMoves().size(); i++) {

            Move a = getLastPos().getMoves().get(i);

            if (a.getOrigin().equals(origin) && a.getDestination().equals(destination))
                valid = a;

        }

        if (valid == null)
            throw new Exception("Invalid move.");

        if (valid.getPromoteType() == '?'
                && (promoteType != 'Q' && promoteType != 'R' && promoteType != 'B' && promoteType != 'N'))
            throw new Exception("Invalid promotion type.");

        Position movePosition = new Position(getLastPos(), valid, this, !getLastPos().isWhite(), true, promoteType);

        if (movePosition.isGivingCheck())
            throw new Exception("Cannot move into check.");

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            throw new Exception("Cannot capture a king.");

        positions.add(movePosition);

        int posNumber = positions.size() - 1;

        if (movePosition.isCheckMate()) {

            result = movePosition.isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN;
            resultReason = REASON_CHECKMATE;

            flipTimer(true, 0);

            fireEvent(new GameEvent(GameEvent.TYPE_MOVE, posNumber - 1, posNumber, getPreviousPos(), getLastPos()));

            markGameOver(movePosition.isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_CHECKMATE);
            return;

        }

        if (movePosition.isInsufficientMaterial()) {

            result = RESULT_DRAW;
            resultReason = REASON_DEAD_INSUFFICIENT_MATERIAL;

            flipTimer(true, 0);

            fireEvent(new GameEvent(GameEvent.TYPE_MOVE, posNumber - 1, posNumber, getPreviousPos(), getLastPos()));

            markGameOver(RESULT_DRAW, REASON_DEAD_INSUFFICIENT_MATERIAL);
            return;

        }

        if (movePosition.getMoves().size() == 0) {

            result = RESULT_DRAW;
            resultReason = REASON_STALEMATE;

            flipTimer(true, 0);

            fireEvent(new GameEvent(GameEvent.TYPE_MOVE, posNumber - 1, posNumber, getPreviousPos(), getLastPos()));

            markGameOver(RESULT_DRAW, REASON_STALEMATE);
            return;

        }

        flipTimer(true, 0);

        fireEvent(new GameEvent(GameEvent.TYPE_MOVE, posNumber - 1, posNumber, getPreviousPos(), getLastPos()));

    }

    public long getTimerTime(boolean color) {

        if (settings.getTimePerSide() <= -1)
            return -1;

        Position p = getLastPos();

        long timer = color ? whiteTimer : blackTimer;

        if (p.isWhite() != color || p.getTimerEnd() > 0)
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

    public void setTimer(boolean white, long time) {

        if (settings.getTimePerSide() <= -1)
            return;

        if (white)
            whiteTimer = time;
        else
            blackTimer = time;

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

        if ((getLastPos().isWhite() && !settings.isWhiteTimerManged())
                || (!getLastPos().isWhite() && !settings.isBlackTimerManaged()))
            setTimer = false;

        if (paused
                || settings.getTimePerSide() <= 0)
            return;

        Position active = getLastPos();
        Position previous = getPreviousPos();

        long currentTime = System.currentTimeMillis();

        if (previous != null && setTimer && pauseTime <= 0 && previous.getTimerEnd() <= 0) {

            if (previous.isWhite())
                whiteTimer -= (currentTime - previous.getSystemTimeStart()) - (settings.getTimePerMove());
            else
                blackTimer -= (currentTime - previous.getSystemTimeStart()) - (settings.getTimePerMove());

            previous.setTimerEnd(previous.isWhite() ? whiteTimer : blackTimer);

        }

        if (pauseTime > 0)
            active.setSystemTimeStart(currentTime - (pauseTime - active.getSystemTimeStart()));
        else if (result == RESULT_IN_PROGRESS && active.getSystemTimeStart() <= 0)
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

    }

    public boolean canPause() {

        return settings.canPause() && !isPaused();

    }

    public void pause() throws Exception {

        if (paused)
            throw new Exception("Game is already paused.");

        paused = true;
        pauseStart = System.currentTimeMillis();

        fireEvent(GameEvent.PAUSED);

    }

    public boolean canResume() {

        return settings.canPause() && isPaused();

    }

    public void resume() throws Exception {

        if (!paused)
            throw new Exception("Game is not paused.");

        paused = false;

        flipTimer(true, pauseStart);

        pauseStart = 0;

        fireEvent(GameEvent.RESUMED);

    }

    public boolean canUndo() {

        return settings.canUndo() && positions.size() > 1;

    }

    public void undo() throws Exception {

        if (!settings.canUndo())
            throw new Exception("Game settings do not allow undo/redo.");

        if (positions.size() <= 1)
            throw new Exception("No move to undo.");

        Position redo = getLastPos();

        positions.remove(positions.size() - 1);

        getLastPos().setRedo(redo);
        redo.setRedoPromote(redo.getMove().getPromoteType());

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromote('?', null);

        redo.setSystemTimeStart(-1);
        redo.setTimerEnd(-1);

        if (result > RESULT_IN_PROGRESS) {
            result = RESULT_IN_PROGRESS;
            resultReason = REASON_IN_PROGRESS;
        }

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

        fireEvent(new GameEvent(GameEvent.TYPE_MOVE, positions.size(), positions.size() - 1, redo, getLastPos()));

    }

    public boolean canRedo() {

        return settings.canUndo() && getLastPos().getRedo() != null;

    }

    public void redo() throws Exception {

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

        fireEvent(new GameEvent(GameEvent.TYPE_MOVE, positions.size() - 2, positions.size() - 1, getPreviousPos(),
                getLastPos()));

    }

    public void addListener(GameListener listener) {

        listeners.add(listener);

    }

    public void fireEvent(GameEvent event) {

        for (GameListener listener : listeners) {

            listener.onPlayerEvent(event);

        }

    }

    public void importPosition(PGNParser PGN) throws Exception {

        /*
         * positions = new ArrayList<Position>();
         * positions.add(new Position(this));
         * 
         * ArrayList<PGNMove> moves = PGN.getParsedMoves();
         * 
         * for (int i = 0; i < moves.size(); i++) {
         * 
         * Move m = new Move(moves.get(i).getMoveText(), getLastPos(),
         * getLastPos().isWhite());
         * 
         * positions.add(new Position(getLastPos(), m, this, !getLastPos().isWhite(),
         * true));
         * 
         * }
         * 
         * if (positions.size() == 1)
         * throw new Exception("Position import failed.");
         * 
         * fireEvent(GameEvent.IMPORTED);
         */

    }

    public String exportPosition() throws Exception {

        return new PGNParser(this, null, true).outputPGN(false);

    }

}
