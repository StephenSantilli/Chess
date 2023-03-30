package game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.PGN.PGNMove;
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
    private GameProperties settings;

    private Player white;
    private Player black;

    private Date start;

    private ArrayList<Chat> messages;

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

    public GameProperties getSettings() {
        return settings;
    }

    public ArrayList<Chat> getMessages() {
        return messages;
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
    public Game(String whiteName, String blackName, String whiteType, String blackType, GameProperties settings)
            throws Exception {

        this.white = new Player(whiteName, whiteType, true);
        this.black = new Player(blackName, blackType, false);

        this.settings = settings;

        positions = new ArrayList<Position>();
        messages = new ArrayList<Chat>();

        if (settings.getFen().equals(GameProperties.DEFAULT_FEN))
            positions.add(new Position(this));
        else
            positions.add(new Position(settings.getFen(), this));

        result = RESULT_NOT_STARTED;
        resultReason = REASON_IN_PROGRESS;

        this.whiteTimer = settings.getTimePerSide() * 1000;
        this.blackTimer = settings.getTimePerSide() * 1000;

        this.listeners = new ArrayList<GameListener>();

    }

    public void startGame() throws Exception {

        if (paused)
            return;

        start = new Date();
        result = RESULT_IN_PROGRESS;

        whiteTimer = settings.getTimePerSide() * 1000;
        blackTimer = settings.getTimePerSide() * 1000;

        // flipTimer(true, 0);
        startTimer();

        if (settings.getTimePerSide() > 0) {

            flagfallChecker = Executors.newScheduledThreadPool(1);
            flagfallChecker.scheduleWithFixedDelay(flagfall, 10, 10, TimeUnit.MILLISECONDS);

        }

        fireEvent(GameEvent.STARTED);

        if (getLastPos().isCheckmate()) {

            markGameOver(getLastPos().isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_CHECKMATE);
            return;

        }

        if (getLastPos().isInsufficientMaterial()) {

            markGameOver(RESULT_DRAW, REASON_DEAD_INSUFFICIENT_MATERIAL);
            return;

        }

        // Stalemate
        if (getLastPos().isStalemate()) {

            markGameOver(RESULT_DRAW, REASON_STALEMATE);
            return;

        }

    }

    public void markGameOver(int result, int resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        if (result <= RESULT_IN_PROGRESS)
            return;

        if (flagfallChecker != null)
            flagfallChecker.shutdownNow();

        // flipTimer(true, 0);
        // endTimer(false);

        fireEvent(GameEvent.OVER);

    }

    public void makeMove(Square origin, Square destination, char promoteType) throws Exception {

        if (paused)
            throw new Exception("Game is paused.");

        if (result != RESULT_IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        Move move = null;
        for (int i = 0; move == null && i < getLastPos().getMoves().size(); i++) {

            Move a = getLastPos().getMoves().get(i);

            if (a.getOrigin().equals(origin) && a.getDestination().equals(destination))
                move = a;

        }

        if (move == null)
            throw new Exception("Invalid move.");

        if (move.getPromoteType() == '?'
                && (promoteType != 'Q' && promoteType != 'R' && promoteType != 'B' && promoteType != 'N'))
            throw new Exception("Invalid promotion type.");

        Position movePosition = new Position(getLastPos(), move, this, !getLastPos().isWhite(), true, promoteType);

        if (movePosition.isGivingCheck())
            throw new Exception("Cannot move into check.");

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            throw new Exception("Cannot capture a king.");

        endTimer(true);

        positions.add(movePosition);

        int posNumber = positions.size() - 1;

        fireEvent(new GameEvent(
                GameEvent.TYPE_MOVE,
                posNumber - 1,
                posNumber,
                getPreviousPos(),
                getLastPos(),
                move,
                move.isWhite()));

        if (movePosition.isCheckmate()) {

            markGameOver(movePosition.isWhite() ? RESULT_BLACK_WIN : RESULT_WHITE_WIN, REASON_CHECKMATE);

        } else if (movePosition.isInsufficientMaterial()) {

            markGameOver(RESULT_DRAW, REASON_DEAD_INSUFFICIENT_MATERIAL);

        } else if (movePosition.isStalemate()) {

            markGameOver(RESULT_DRAW, REASON_STALEMATE);

        } else
            startTimer();

    }

    public long getTimerTime(boolean color) {

        if (settings.getTimePerSide() <= -1)
            return -1;

        Position p = getLastPos();

        final long timer = color ? whiteTimer : blackTimer;

        if (p.isWhite() != color || p.getTimerEnd() > 0 || p.getSystemTimeStart() <= 0)
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

    public long getTimer(boolean white) {

        if (settings.getTimePerSide() <= -1)
            return -1;

        if (white)
            return whiteTimer;
        else
            return blackTimer;

    }

    public void decTimer(boolean white, long dec) {

        if (settings.getTimePerSide() <= -1)
            return;

        if (white)
            whiteTimer -= dec;
        else
            blackTimer -= dec;

    }

    public void setTimer(boolean white, long time) {

        if (settings.getTimePerSide() <= -1)
            return;

        if (white)
            whiteTimer = time;
        else
            blackTimer = time;

    }

    private void saveTimer() {

        if (settings.getTimePerSide() <= 0)
            return;

        final Position lastPos = getLastPos();

        decTimer(lastPos.isWhite(), System.currentTimeMillis() - lastPos.getSystemTimeStart());

    }

    private void endTimer(boolean addTimePerMove) {

        if (settings.getTimePerSide() <= 0)
            return;

        final Position lastPos = getLastPos();

        if ((lastPos.isWhite() && !settings.isWhiteTimerManged())
                || (!lastPos.isWhite() && !settings.isBlackTimerManaged()))
            return;

        if (lastPos.getSystemTimeStart() <= 0)
            return;

        decTimer(lastPos.isWhite(), System.currentTimeMillis() - lastPos.getSystemTimeStart()
                + ((addTimePerMove && settings.getTimePerMove() > -1) ? -(settings.getTimePerMove() * 1000) : 0));
        lastPos.setTimerEnd(lastPos.isWhite() ? whiteTimer : blackTimer);

    }

    private void startTimer() {

        if (settings.getTimePerSide() <= 0)
            return;

        final Position lastPos = getLastPos();

        if (lastPos.getSystemTimeStart() > 0)
            return;

        lastPos.setSystemTimeStart(System.currentTimeMillis());

    }

    public boolean canPause() {

        return result == RESULT_IN_PROGRESS && settings.canPause() && !isPaused();

    }

    public void pause() throws Exception {

        if (paused)
            throw new Exception("Game is already paused.");

        paused = true;

        saveTimer();
        getLastPos().setSystemTimeStart(-1);

        fireEvent(GameEvent.PAUSED);

    }

    public boolean canResume() {

        return result == RESULT_IN_PROGRESS && settings.canPause() && isPaused();

    }

    public void resume() throws Exception {

        if (!paused)
            throw new Exception("Game is not paused.");

        paused = false;

        startTimer();

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
            blackTimer = prev == null ? settings.getTimePerSide() * 1000 : prev.getTimerEnd();
            if (settings.getTimePerMove() > 0)
                whiteTimer -= settings.getTimePerMove() * 1000;
        } else {
            whiteTimer = prev == null ? settings.getTimePerSide() * 1000 : prev.getTimerEnd();
            if (settings.getTimePerMove() > 0)
                blackTimer -= settings.getTimePerMove() * 1000;
        }

        getLastPos().setSystemTimeStart(-1);
        getLastPos().setTimerEnd(-1);

        fireEvent(new GameEvent(
                GameEvent.TYPE_MOVE,
                positions.size(),
                positions.size() - 1,
                redo,
                getLastPos(),
                getLastPos().getMove(),
                !getLastPos().isWhite()));

        startTimer();

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

        endTimer(true);

        positions.add(redo);

        if (redo.getRedoPromote() != '0')
            redo.setPromote(redo.getRedoPromote(), this);

        fireEvent(new GameEvent(
                GameEvent.TYPE_MOVE,
                positions.size() - 2,
                positions.size() - 1,
                getPreviousPos(),
                getLastPos(),
                getLastPos().getMove(),
                getLastPos().getMove().isWhite()));

        startTimer();

    }

    public boolean canDrawOffer() {

        return result == RESULT_IN_PROGRESS && getLastPos().getDrawOfferer() == Position.NO_OFFER;

    }

    public void sendDrawOffer(boolean offererWhite) throws Exception {

        if (!canDrawOffer())
            throw new Exception("Cannot offer a draw.");

        getLastPos().setDrawOfferer(offererWhite ? Position.WHITE : Position.BLACK);
        fireEvent(GameEvent.DRAW_OFFER);

        sendMessage(new Chat(getPlayer(offererWhite), new Date().getTime(),
                getPlayer(offererWhite).getName() + " sent a draw offer.", true));

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

        sendMessage(new Chat(getPlayer(getLastPos().getDrawOfferer() == Position.WHITE), new Date().getTime(),
                getPlayer(getLastPos().getDrawOfferer() == Position.WHITE).getName() + " accepted the draw offer."));

    }

    public void declineDrawOffer() throws Exception {

        if (canDrawOffer())
            throw new Exception("No draw to decline.");

        final boolean offererWhite = getLastPos().getDrawOfferer() == Position.WHITE;

        getLastPos().setDrawOfferer(Position.NO_OFFER);

        fireEvent(new GameEvent(GameEvent.TYPE_DRAW_DECLINED, !offererWhite));

        sendMessage(new Chat(getPlayer(!offererWhite), new Date().getTime(),
                getPlayer(!offererWhite).getName() + " declined the draw offer.", true));

    }

    public void sendMessage(Chat message) {

        messages.add(message);
        fireEvent(new GameEvent(message));

    }

    public void importPosition(PGNParser PGN) throws Exception {

        if (result != RESULT_NOT_STARTED)
            throw new Exception("Cannot import a game after it has already started!");

        positions = new ArrayList<Position>();
        positions.add(new Position(this));

        ArrayList<PGNMove> pMoves = PGN.getParsedMoves();

        for (int i = 0; i < pMoves.size(); i++) {
            String m = pMoves.get(i).getMoveText();
            try {
                char promote = m.charAt(m.length() - 1);
                if (!((promote + "").matches("[QRBN]")))
                    promote = '0';

                positions.add(new Position(getLastPos(), getLastPos().getMoveByPGN(m), this, !getLastPos().isWhite(),
                        true, promote));
            } catch (Exception e) {
                throw new Exception("Error at move " + i + ", \"" + m + "\". " + e.getMessage());
            }

        }

        if (positions.size() == 1)
            throw new Exception("Position import failed.");

        fireEvent(GameEvent.IMPORTED);

    }

    public String exportPosition(boolean includeTags, boolean includeClock) throws Exception {

        Map<String, String> tags = new HashMap<>();

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        if (start != null)

            tags.put("Date", df.format(start));

        tags.put("White", getPlayer(true).getName());
        tags.put("Black", getPlayer(false).getName());

        switch (result) {
            case RESULT_DRAW:
                tags.put("Result", "1/2-1/2");
                break;
            case RESULT_WHITE_WIN:
                tags.put("Result", "1-0");
                break;
            case RESULT_BLACK_WIN:
                tags.put("Result", "0-1");
                break;
            default:
                tags.put("Result", "*");
        }

        if (settings.getTimePerSide() <= -1)
            tags.put("TimeControl", "-");
        else
            tags.put("TimeControl",
                    settings.getTimePerSide() + (settings.getTimePerMove() > 0 ? "+" + settings.getTimePerMove() : ""));

        final Player white = getPlayer(true);
        final Player black = getPlayer(false);

        if (!white.getType().equals(""))
            tags.put("WhiteType", white.getType());

        if (!black.getType().equals(""))
            tags.put("BlackType", black.getType());

        return new PGNParser(this, tags, includeClock).outputPGN(includeTags);

    }

    public void addListener(GameListener listener) {

        listeners.add(listener);

    }

    public void fireEvent(GameEvent event) {

        for (GameListener listener : listeners) {

            listener.onPlayerEvent(event);

        }

    }

    // /**
    // * @deprecated
    // * Flips the timer to the color of {@link #getActivePos()}.
    // *
    // * <p>
    // * If {@code setTimer}
    // * is {@code true}, the {@link Position#timerEnd} for the previous
    // * position will
    // * be set, and either {@link #whiteTimer} or {@link #blackTimer}
    // * will be set.
    // * Will also reset the timer task to schedule flagfall.
    // *
    // * <p>
    // * If the game is over, ({@link #result} does not equal {@code 0},)
    // * and
    // * {@code setTimer} is true, then the flagfall timer will be stopped
    // * and a new
    // * one will not be started.
    // *
    // * @param setTimer Whether or not the timer should be set.
    // * @param pauseTime The time the game was paused at, if resuming. Should be
    // * {@code 0} otherwise.
    // */
    // @Deprecated
    // private void flipTimer(boolean setTimer, long pauseTime) {

    // if ((getLastPos().isWhite() && !settings.isWhiteTimerManged())
    // || (!getLastPos().isWhite() && !settings.isBlackTimerManaged()))
    // setTimer = false;

    // if (paused
    // || settings.getTimePerSide() <= 0)
    // return;

    // final Position active = getLastPos();
    // final Position previous = getPreviousPos();

    // final long currentTime = System.currentTimeMillis();

    // if (previous != null && setTimer && pauseTime <= 0 && previous.getTimerEnd()
    // <= 0) {

    // if (previous.isWhite())
    // whiteTimer -= (currentTime - previous.getSystemTimeStart()) -
    // (settings.getTimePerMove() * 1000);
    // else
    // blackTimer -= (currentTime - previous.getSystemTimeStart()) -
    // (settings.getTimePerMove() * 1000);

    // previous.setTimerEnd(previous.isWhite() ? whiteTimer : blackTimer);

    // }

    // if (pauseTime > 0)
    // active.setSystemTimeStart(currentTime - (pauseTime -
    // active.getSystemTimeStart()));
    // else if (result == RESULT_IN_PROGRESS && active.getSystemTimeStart() <= 0)
    // active.setSystemTimeStart(currentTime);

    // if (result > RESULT_IN_PROGRESS && active.getSystemTimeStart() > 0) {

    // if (active.isWhite()) {

    // whiteTimer -= (currentTime - active.getSystemTimeStart());
    // active.setTimerEnd(whiteTimer);

    // } else {

    // blackTimer -= (currentTime - active.getSystemTimeStart());
    // active.setTimerEnd(blackTimer);

    // }

    // }

    // }

}
