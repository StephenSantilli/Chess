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

    /**
     * The settings of the game.
     */
    private GameSettings settings;

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
     */
    private Result result;

    /**
     * <p>
     * The reason for the result of the game.
     */
    private ResultReason resultReason;

    /**
     * The system time that the active timer was started.
     */
    private long timerStart;

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
            markGameOver(Result.BLACK_WIN, ResultReason.FLAGFALL);

        if (getTimerTime(false) <= 0)
            markGameOver(Result.WHITE_WIN, ResultReason.FLAGFALL);

    };

    public GameSettings getSettings() {
        return settings;
    }

    public ArrayList<Chat> getMessages() {
        return messages;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public Result getResult() {
        return result;
    }

    public ResultReason getResultReason() {
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
    public Game(String whiteName, String blackName, String whiteType, String blackType, GameSettings settings)
            throws Exception {

        this.white = new Player(whiteName, whiteType, true);
        this.black = new Player(blackName, blackType, false);

        positions = new ArrayList<Position>();
        listeners = new ArrayList<GameListener>();
        messages = new ArrayList<Chat>();

        this.settings = settings;

        result = Result.NOT_STARTED;
        resultReason = ResultReason.IN_PROGRESS;

        if (settings.getFen().equals(GameSettings.DEFAULT_FEN))
            positions.add(new Position(this));
        else
            positions.add(new Position(settings.getFen(), this));

    }

    public Game(PGNParser pgn, GameSettings settings, boolean overridePGNSettings) throws Exception {

        positions = new ArrayList<Position>();
        messages = new ArrayList<Chat>();
        this.listeners = new ArrayList<GameListener>();
        result = Result.NOT_STARTED;
        resultReason = ResultReason.IN_PROGRESS;

        // TODO: support player type
        final String whiteName = pgn.getTags().getOrDefault("White", "White");
        this.white = new Player((whiteName.equals("") ? "White" : whiteName),
                pgn.getTags().getOrDefault("WhiteType", Player.HUMAN), true);

        final String blackName = pgn.getTags().getOrDefault("Black", "Black");
        this.black = new Player((blackName.equals("") ? "Black" : blackName),
                pgn.getTags().getOrDefault("BlackType", Player.HUMAN), false);

        this.settings = new GameSettings(overridePGNSettings ? settings.getTimePerSide() : pgn.getTimePerSide(),
                overridePGNSettings ? settings.getTimePerMove() : pgn.getTimePerMove(),
                settings.canPause(),
                settings.canUndo(),
                settings.isWhiteTimerManged(),
                settings.isBlackTimerManaged());

        // TODO: support setup tag, so you can start from non-default positions
        final String setup = pgn.getTags().getOrDefault("SetUp", "");
        final String fen = pgn.getTags().getOrDefault("FEN", "");
        if (setup.equals("1") && !fen.equals("")) {
            positions.add(new Position(fen, this));
        } else
            positions.add(new Position(this));

        ArrayList<PGNMove> pMoves = pgn.getParsedMoves();

        for (int i = 0; i < pMoves.size(); i++) {

            String m = pMoves.get(i).getMoveText();

            try {

                char promote = m.charAt(m.length() - 1);

                if (!((promote + "").matches("[QRBN]")))
                    promote = '0';

                positions.add(new Position(getLastPos(), getLastPos().getMoveByPGN(m), this, !getLastPos().isWhite(),
                        true, promote));

                getPreviousPos().setTimerEnd(
                        pMoves.get(i).getTimerEnd() - calcTimerDelta(calcMovesPerSide(getPreviousPos().isWhite())));

            } catch (Exception e) {
                throw new Exception("Error importing PGN at move " + i + ", \"" + m + "\". " + e.getMessage());
            }

        }

        // if (positions.size() == 1)
        // throw new Exception("Position import failed.");

        fireEvent(GameEvent.IMPORTED);

    }

    /**
     * Calculates the number of moves each side has completed.
     * 
     * @param white
     * @return
     */
    public int calcMovesPerSide(boolean white) {
        final boolean isTurn = getLastPos().isWhite() == white;
        int moveCount = (int) Math.ceil(getLastPos().getMoveNumber() / 2.0);

        if (isTurn && settings.isWhiteStarts() != white)
            --moveCount;

        return moveCount;

    }

    /**
     * Calculates, with the supplied {@code moveCount}, how many additional seconds
     * should be added to the clock when {@link GameSettings#getTimePerMove()} is
     * greater than {@code 0}.
     * 
     * @param moveCount
     * @return
     */
    public long calcTimerDelta(int moveCount) {
        return moveCount * (settings.getTimePerMove() * 1000);
    }

    /**
     * Gets the elapsed time of the current running timer. Will be {@code 0} if the
     * timer has not been started.
     * 
     * @return
     */
    private long getElapsed() {
        return timerStart >= 0 ? System.currentTimeMillis() - timerStart : 0;
    }

    /**
     * Gets the {@code timerEnd} of the requested color's last completed turn.
     * 
     * @param white
     * @return
     */
    public long getPrevTimerEnd(boolean white) {

        final boolean isTurn = getLastPos().isWhite() == white;

        if (isTurn && getLastPos().getTimerEnd() > -1)
            return getLastPos().getTimerEnd();

        long lastTimerEnd = settings.getTimePerSide() * 1000;

        int index = isTurn ? positions.size() - 3 : positions.size() - 2;

        if (index >= 0)
            lastTimerEnd = positions.get(index).getTimerEnd();

        return lastTimerEnd;

    }

    /**
     * Gets the current, live time remaining the requested color has.
     * 
     * @param white
     * @return
     */
    public long getTimerTime(boolean white) {

        final boolean isTurn = getLastPos().isWhite() == white;

        long lastTimerEnd = getPrevTimerEnd(white);

        final int moveCount = calcMovesPerSide(white);

        lastTimerEnd += calcTimerDelta(moveCount);

        if (isTurn)
            lastTimerEnd -= getElapsed();

        if (lastTimerEnd < 0)
            return 0;

        return lastTimerEnd;

    }

    /**
     * Starts the timer.
     * 
     * @return
     */
    public void startTimer() {

        timerStart = System.currentTimeMillis();

    }

    /**
     * Stops the current timer and updates the current position's {@code timerEnd}
     * property
     */
    public void stopTimer() {

        final long current = getPrevTimerEnd(getLastPos().isWhite());

        getLastPos().setTimerEnd(current - getElapsed());

        timerStart = -1;

    }

    public void startGame() throws Exception {

        if (paused)
            return;

        start = new Date();
        result = Result.IN_PROGRESS;

        startTimer();

        if (settings.getTimePerSide() > 0) {

            flagfallChecker = Executors.newScheduledThreadPool(1);
            flagfallChecker.scheduleWithFixedDelay(flagfall, 10, 10, TimeUnit.MILLISECONDS);

        }

        fireEvent(GameEvent.STARTED);

        if (getLastPos().isCheckmate()) {

            markGameOver(getLastPos().isWhite() ? Result.BLACK_WIN : Result.WHITE_WIN, ResultReason.CHECKMATE);
            return;

        }

        if (getLastPos().isInsufficientMaterial()) {

            markGameOver(Result.DRAW, ResultReason.DEAD_INSUFFICIENT_MATERIAL);
            return;

        }

        if (getLastPos().isStalemate()) {

            markGameOver(Result.DRAW, ResultReason.STALEMATE);
            return;

        }

    }

    public void markGameOver(Result result, ResultReason resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        if (result == Result.NOT_STARTED || result == Result.IN_PROGRESS)
            return;

        if (flagfallChecker != null)
            flagfallChecker.shutdownNow();

        stopTimer();

        fireEvent(GameEvent.OVER);

    }

    public void makeMove(Square origin, Square destination, char promoteType) throws Exception {

        if (paused)
            throw new Exception("Game is paused.");

        if (result != Result.IN_PROGRESS)
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

        stopTimer();

        positions.add(movePosition);

        fireEvent(new GameEvent(
                GameEvent.TYPE_MOVE,
                positions.size() - 2,
                positions.size() - 1,
                getPreviousPos(),
                getLastPos(),
                move,
                move.isWhite()));

        if (movePosition.isCheckmate()) {

            markGameOver((movePosition.isWhite() ? Result.BLACK_WIN : Result.WHITE_WIN), ResultReason.CHECKMATE);

        } else if (movePosition.isInsufficientMaterial()) {

            markGameOver(Result.DRAW, ResultReason.DEAD_INSUFFICIENT_MATERIAL);

        } else if (movePosition.isStalemate()) {

            markGameOver(Result.DRAW, ResultReason.STALEMATE);

        } else
            startTimer();

    }

    public boolean canPause() {

        return result == Result.IN_PROGRESS && settings.canPause() && !isPaused();

    }

    public void pause() throws Exception {

        if (paused)
            throw new Exception("Game is already paused.");

        paused = true;

        stopTimer();

        fireEvent(GameEvent.PAUSED);

    }

    public boolean canResume() {

        return result == Result.IN_PROGRESS && settings.canPause() && isPaused();

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

        stopTimer();

        Position redo = getLastPos();

        positions.remove(positions.size() - 1);

        getLastPos().setRedo(redo);
        redo.setRedoPromote(redo.getMove().getPromoteType());

        // getLastPos().setTimerEnd(-1);

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromote('?', null);

        redo.setRedoTimerEnd(getLastPos().getTimerEnd());

        if (result != Result.NOT_STARTED && result != Result.IN_PROGRESS) {
            result = Result.IN_PROGRESS;
            resultReason = ResultReason.IN_PROGRESS;
        }

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

        stopTimer();

        getLastPos().setTimerEnd(redo.getRedoTimerEnd());

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

        return result == Result.IN_PROGRESS && getLastPos().getDrawOfferer() == Position.NO_OFFER;

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

        if (result != Result.IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        if (getLastPos().getDrawOfferer() == Position.NO_OFFER)
            throw new Exception("No draw offer.");

        if (!canDrawOffer())
            throw new Exception("Cannot accept draw.");

        markGameOver(Result.DRAW,
                (getLastPos().getDrawOfferer() == Position.WHITE)
                        ? ResultReason.WHITE_OFFERED_DRAW
                        : ResultReason.BLACK_OFFERED_DRAW);

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

    public String exportPosition(boolean includeTags, boolean includeClock) throws Exception {

        Map<String, String> tags = new HashMap<>();

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        if (start != null)

            tags.put("Date", df.format(start));

        tags.put("White", getPlayer(true).getName());
        tags.put("Black", getPlayer(false).getName());

        switch (result) {
            case DRAW:
                tags.put("Result", "1/2-1/2");
                break;
            case WHITE_WIN:
                tags.put("Result", "1-0");
                break;
            case BLACK_WIN:
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

}
