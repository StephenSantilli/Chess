package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import game.GameEvent.Type;
import game.PGN.PGNMove;
import game.PGN.PGNParser;

public class Game {

    public static void main(String[] args) throws IOException {

    }

    public enum Result {

        NOT_STARTED,
        IN_PROGRESS,
        WHITE_WIN,
        BLACK_WIN,
        DRAW,
        TERMINATED

    }

    public enum Reason {

        IN_PROGRESS,
        CHECKMATE,
        FLAGFALL,
        WHITE_OFFERED_DRAW,
        BLACK_OFFERED_DRAW,
        STALEMATE,
        DEAD_INSUFFICIENT_MATERIAL,
        DEAD_NO_POSSIBLE_MATE,
        REPETITION,
        FIFTY_MOVE,
        RESIGNATION,
        OTHER;

    }

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
    private Reason resultReason;

    /**
     * The system time that the active timer was started.
     */
    private long timerStart;

    /**
     * Whether or not the game is paused.
     */
    private boolean paused;

    // TODO: use this instead of position draw
    /**
     * The player that offered a draw. {@code null} if no draw has been offered or
     * the previous draw offer has been declined.
     */
    private Player drawOfferer;

    /**
     * The service that checks for flagfall in the background.
     */
    private ScheduledExecutorService flagfallChecker;

    /**
     * The flagfall checker task.
     */
    Runnable flagfall = () -> {

        if (getTimerTime(true) <= 0)
            markGameOver(Game.Result.BLACK_WIN, Game.Reason.FLAGFALL);

        if (getTimerTime(false) <= 0)
            markGameOver(Game.Result.WHITE_WIN, Game.Reason.FLAGFALL);

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

    public Reason getResultReason() {
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
     * Generates a Chess960 starting position using the algorithm found here:
     * https://en.wikipedia.org/wiki/Fischer_random_chess_numbering_scheme#Direct_derivation
     * 
     * @return A Chess960 starting position.
     */
    public static Position generate960Start() throws Exception {

        char[] pcs = new char[8];

        // Generate random number from 0 to 959
        Random rand = new Random();
        final int n = rand.nextInt(0, 960);

        final int n2 = n / 4;
        final int b1 = n % 4;

        // Place light-square bishop
        switch (b1) {
            case 0:
                pcs[1] = 'B';
                break;
            case 1:
                pcs[3] = 'B';
                break;
            case 2:
                pcs[5] = 'B';
                break;
            case 3:
                pcs[7] = 'B';
                break;
        }

        final int n3 = n2 / 4;
        final int b2 = n2 % 4;

        // Place dark-square bishop
        switch (b2) {
            case 0:
                pcs[0] = 'B';
                break;
            case 1:
                pcs[2] = 'B';
                break;
            case 2:
                pcs[4] = 'B';
                break;
            case 3:
                pcs[6] = 'B';
                break;
        }

        final int n4 = n3 / 6;
        final int q = n3 % 6;

        // Place queen in the [q]th open square
        int iq = 0;
        int zq = 0;
        while (iq <= q) {

            if (pcs[zq] == '\u0000')
                iq++;

            if (iq <= q)
                zq++;

        }

        pcs[zq] = 'Q';

        // Place Knights based on the N5N table
        final int[] n5nTable1 = { 0, 0, 0, 0, 1, 1, 1, 2, 2, 3 };
        final int[] n5nTable2 = { 1, 2, 3, 4, 2, 3, 4, 3, 4, 4 };

        final int knight1 = n5nTable1[n4];
        final int knight2 = n5nTable2[n4];

        // Place first knight in the [knight1]th open square
        int in1 = 0;
        int zn1 = 0;
        while (in1 <= knight1) {

            if (pcs[zn1] == '\u0000')
                in1++;

            if (in1 <= knight1)
                zn1++;

        }

        // Place second knight in the [knight2]th open square
        int in2 = 0;
        int zn2 = 0;
        while (in2 <= knight2) {

            if (pcs[zn2] == '\u0000')
                in2++;

            if (in2 <= knight2)
                zn2++;

        }

        pcs[zn1] = 'N';
        pcs[zn2] = 'N';

        int x = 0;

        // Place rook in first open square
        while (x < 8 && pcs[x] != '\u0000') {
            ++x;
        }

        pcs[x] = 'R';
        ++x;

        // Place king in middle open square
        while (x < 8 && pcs[x] != '\u0000') {
            ++x;
        }

        pcs[x] = 'K';
        ++x;

        // Place rook in last open square
        while (x < 8 && pcs[x] != '\u0000') {
            ++x;
        }

        pcs[x] = 'R';

        String fenRow = new String(pcs);

        String fen = fenRow.toLowerCase() + "/pppppppp/8/8/8/8/PPPPPPPP/" + fenRow + " w KQkq - 0 1";

        return new Position(fen);

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

        result = Game.Result.NOT_STARTED;
        resultReason = Game.Reason.IN_PROGRESS;

        if (settings.getFen().equals(GameSettings.DEFAULT_FEN))
            positions.add(new Position());
        else
            positions.add(new Position(settings.getFen()));

    }

    public Game(PGNParser pgn, GameSettings settings, boolean overridePGNSettings) throws Exception {

        positions = new ArrayList<Position>();
        messages = new ArrayList<Chat>();
        this.listeners = new ArrayList<GameListener>();
        result = Game.Result.NOT_STARTED;
        resultReason = Game.Reason.IN_PROGRESS;

        final String whiteName = pgn.getTags().getOrDefault("White", "White");
        final String whiteType = pgn.getTags().getOrDefault("WhiteType", Player.HUMAN);
        this.white = new Player(whiteName, whiteType, true);

        final String blackName = pgn.getTags().getOrDefault("Black", "Black");
        final String blackType = pgn.getTags().getOrDefault("BlackType", Player.HUMAN);
        this.black = new Player(blackName, blackType, false);

        final String setup = pgn.getTags().getOrDefault("SetUp", "");
        final String fen = pgn.getTags().getOrDefault("FEN", "");

        if (setup.equals("1") && !fen.equals("")) {
            positions.add(new Position(fen));
        } else
            positions.add(new Position());

        this.settings = new GameSettings(setup.equals("1") && !fen.equals("") ? fen : GameSettings.DEFAULT_FEN,
                overridePGNSettings ? settings.getTimePerSide() : pgn.getTimePerSide(),
                overridePGNSettings ? settings.getTimePerMove() : pgn.getTimePerMove(),
                settings.canPause(),
                settings.canUndo(),
                settings.isWhiteTimerManged(),
                settings.isBlackTimerManaged());

        ArrayList<PGNMove> pMoves = pgn.getMoves();

        for (int i = 0; i < pMoves.size(); i++) {

            String m = pMoves.get(i).getMoveText();

            try {

                char promote = m.charAt(m.length() - 1);

                if (!((promote + "").matches("[QRBN]")))
                    promote = '0';

                positions.add(new Position(getLastPos(), getLastPos().getMoveBySAN(m), !getLastPos().isWhite(),
                        true, promote));

                getPreviousPos().setTimerEnd(
                        pMoves.get(i).getTimerEnd()
                                - calcTimerDelta(calcMovesPerSide(getPreviousPos().isWhite(), positions.size() - 1)));

            } catch (Exception e) {
                throw new Exception("Error importing PGN at move " + i + ", \"" + m + "\". " + e.getMessage());
            }

        }

        fireEvent(new GameEvent(Type.IMPORTED));

        final String res = pgn.getTags().getOrDefault("Result", "*");
        switch (res) {
            case "1/2-1/2":
                result = Result.DRAW;
                resultReason = Reason.OTHER;
                break;
            case "1-0":
                result = Result.WHITE_WIN;
                resultReason = Reason.OTHER;
                break;
            case "0-1":
                result = Result.BLACK_WIN;
                resultReason = Reason.OTHER;
                break;
        }

    }

    /**
     * Calculates the number of moves each side has completed.
     * 
     * @param white
     * @return
     */
    public int calcMovesPerSide(boolean white, int position) {

        final Position pos = positions.get(position);

        final boolean isTurn = pos.isWhite() == white;
        int moveCount = (int) Math.ceil(pos.getMoveNumber() / 2.0);

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
    public long getPrevTimerEnd(boolean white, int position) {

        final Position pos = positions.get(position);

        final boolean isTurn = pos.isWhite() == white;

        long lastTimerEnd = settings.getTimePerSide() * 1000;

        int index = isTurn ? position - 2 : position - 1;

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
        return getTimerTime(white, positions.size() - 1);
    }

    public long getTimerTime(boolean white, int position) {

        final Position pos = positions.get(position);

        final boolean isTurn = pos.isWhite() == white;

        long lastTimerEnd = getPrevTimerEnd(white, position);

        final int moveCount = calcMovesPerSide(white, position);

        if (isTurn && pos.getTimerEnd() > -1)
            lastTimerEnd = pos.getTimerEnd();

        lastTimerEnd += calcTimerDelta(Math.max(moveCount, 0));

        if (isTurn && position == positions.size() - 1)
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

        long current = getPrevTimerEnd(getLastPos().isWhite(), positions.size() - 1);

        if (getLastPos().getTimerEnd() > -1)
            current = getLastPos().getTimerEnd();

        getLastPos().setTimerEnd(current - getElapsed());

        timerStart = -1;

    }

    public void startGame() throws Exception {

        if (paused)
            return;

        start = new Date();

        final String res = result.toString();
        final String reas = resultReason.toString();

        result = Result.IN_PROGRESS;
        resultReason = Reason.IN_PROGRESS;

        startTimer();

        if (settings.getTimePerSide() > 0) {

            flagfallChecker = Executors.newScheduledThreadPool(1);
            flagfallChecker.scheduleWithFixedDelay(flagfall, 10, 10, TimeUnit.MILLISECONDS);

        }

        fireEvent(new GameEvent(Type.STARTED));

        checkGameOver();

        if (result == Result.IN_PROGRESS && Result.valueOf(res) != Result.IN_PROGRESS && Result
                .valueOf(res) != Result.NOT_STARTED)
            markGameOver(Result.valueOf(res), Reason.valueOf(reas));

    }

    public void checkGameOver() {

        if (getLastPos().isCheckmate())
            markGameOver(getLastPos().isWhite() ? Game.Result.BLACK_WIN : Game.Result.WHITE_WIN, Game.Reason.CHECKMATE);

        else if (getLastPos().isInsufficientMaterial())
            markGameOver(Game.Result.DRAW, Game.Reason.DEAD_INSUFFICIENT_MATERIAL);

        else if (getLastPos().isStalemate())
            markGameOver(Game.Result.DRAW, Game.Reason.STALEMATE);

    }

    public void markGameOver(Result result, Reason resultReason) {

        this.result = result;
        this.resultReason = resultReason;

        if (result == Game.Result.NOT_STARTED || result == Game.Result.IN_PROGRESS)
            return;

        if (flagfallChecker != null)
            flagfallChecker.shutdownNow();

        stopTimer();
        fireEvent(new GameEvent(Type.OVER));

    }

    public void makeMove(Square origin, Square destination, char promoteType) throws Exception {
        this.makeMove(origin, destination, promoteType, false);
    }

    public void makeMove(Square origin, Square destination, char promoteType, boolean isCastle) throws Exception {

        if (paused)
            throw new Exception("Game is paused.");

        if (result != Game.Result.IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        Move move = null;
        for (int i = 0; move == null && i < getLastPos().getMoves().size(); i++) {

            Move a = getLastPos().getMoves().get(i);

            if (a.getOrigin().equals(origin) && a.getDestination().equals(destination) && isCastle == a.isCastle())
                move = a;
            else if (a.isCastle() && getLastPos().getPieceAtSquare(destination) != null
                    && getLastPos().getPieceAtSquare(destination).equals(a.getRook())) {
                move = a;
            }

        }

        if (move == null)
            throw new Exception("Invalid move.");

        if (move.getPromoteType() == '?'
                && (promoteType != 'Q' && promoteType != 'R' && promoteType != 'B' && promoteType != 'N'))
            throw new Exception("Invalid promotion type.");

        Position movePosition = new Position(getLastPos(), move, !getLastPos().isWhite(), true, promoteType);

        if (movePosition.isGivingCheck())
            throw new Exception("Cannot move into check.");

        if (movePosition.getMove().isCapture() && movePosition.getMove().getCapturePiece().getCode() == 'K')
            throw new Exception("Cannot capture a king.");

        stopTimer();

        positions.add(movePosition);

        fireEvent(new GameEvent(
                Type.MOVE,
                positions.size() - 2,
                positions.size() - 1,
                getPreviousPos(),
                getLastPos(),
                move,
                move.isWhite()));

        checkGameOver();

        if (result == Game.Result.IN_PROGRESS)
            startTimer();

    }

    public boolean canPause() {

        return result == Game.Result.IN_PROGRESS && settings.canPause() && !isPaused();

    }

    public void pause() throws Exception {

        if (paused)
            throw new Exception("Game is already paused.");

        paused = true;

        stopTimer();

        fireEvent(new GameEvent(Type.PAUSED));

    }

    public boolean canResume() {

        return result == Game.Result.IN_PROGRESS && settings.canPause() && isPaused();

    }

    public void resume() throws Exception {

        if (!paused)
            throw new Exception("Game is not paused.");

        paused = false;

        startTimer();

        fireEvent(new GameEvent(Type.RESUMED));

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

        if (redo.getMove().getPromoteType() != '0')
            redo.setPromote('?');

        redo.setRedoTimerEnd(getLastPos().getTimerEnd());

        if (result != Game.Result.NOT_STARTED && result != Game.Result.IN_PROGRESS) {
            result = Game.Result.IN_PROGRESS;
            resultReason = Game.Reason.IN_PROGRESS;
        }

        fireEvent(new GameEvent(
                Type.MOVE,
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
            redo.setPromote(redo.getRedoPromote());

        fireEvent(new GameEvent(
                Type.MOVE,
                positions.size() - 2,
                positions.size() - 1,
                getPreviousPos(),
                getLastPos(),
                getLastPos().getMove(),
                getLastPos().getMove().isWhite()));

        startTimer();

    }

    public boolean canDrawOffer() {

        return result == Game.Result.IN_PROGRESS && getLastPos().getDrawOfferer() == Position.NO_OFFER;

    }

    public void sendDrawOffer(boolean offererWhite) throws Exception {

        if (!canDrawOffer())
            throw new Exception("Cannot offer a draw.");

        getLastPos().setDrawOfferer(offererWhite ? Position.WHITE : Position.BLACK);
        fireEvent(new GameEvent(Type.DRAW_OFFER));

        sendMessage(new Chat(getPlayer(offererWhite), new Date().getTime(),
                getPlayer(offererWhite).getName() + " sent a draw offer.", true));

    }

    public void acceptDrawOffer() throws Exception {

        if (result != Game.Result.IN_PROGRESS)
            throw new Exception("Game is not in progress.");

        if (getLastPos().getDrawOfferer() == Position.NO_OFFER)
            throw new Exception("No draw offer.");

        if (!canDrawOffer())
            throw new Exception("Cannot accept draw.");

        markGameOver(Game.Result.DRAW,
                (getLastPos().getDrawOfferer() == Position.WHITE)
                        ? Game.Reason.WHITE_OFFERED_DRAW
                        : Game.Reason.BLACK_OFFERED_DRAW);

        sendMessage(new Chat(getPlayer(getLastPos().getDrawOfferer() == Position.WHITE), new Date().getTime(),
                getPlayer(getLastPos().getDrawOfferer() == Position.WHITE).getName() + " accepted the draw offer."));

    }

    public void declineDrawOffer() throws Exception {

        if (canDrawOffer())
            throw new Exception("No draw to decline.");

        final boolean offererWhite = getLastPos().getDrawOfferer() == Position.WHITE;

        getLastPos().setDrawOfferer(Position.NO_OFFER);

        fireEvent(new GameEvent(Type.DRAW_DECLINED, !offererWhite));

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

        if (!settings.getFen().equals(GameSettings.DEFAULT_FEN)) {
            tags.put("SetUp", "1");
            tags.put("FEN", settings.getFen());
        }

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
