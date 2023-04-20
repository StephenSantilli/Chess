package game;

/**
 * The settings for the {@link Game}.
 */
public class GameSettings {

    /** The default starting position for standard chess. */
    public static final String DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    /** The starting position of the game, in Forsyth-Edwards Notation (FEN). */
    private final String fen;

    // TODO: allow black to make first move
    /**
     * Whether or not white makes the first move.
     * 
     * <p>
     * <b>NOT CURRENTLY SUPPORTED</b>
     */
    private final boolean whiteStarts;

    /**
     * The time, in seconds, each side has in total. Should be {@code 0} if no
     * time control used.
     */
    private final long timePerSide;

    /**
     * The time, in seconds, each side gains per move made. Should be
     * {@code 0} if no time control is used or if no time is added per move.
     */
    private final long timePerMove;

    /**
     * Whether or not pausing is allowed.
     */
    private final boolean canPause;

    /**
     * Whether or not undo/redo is allowed.
     */
    private final boolean canUndo;

    /**
     * Whether or not white's timer should be automatically flipped/managed.
     */
    private final boolean whiteTimerManaged;

    /**
     * Whether or not black's timer should be automatically flipped/managed.
     */
    private final boolean blackTimerManaged;

    /**
     * @see #whiteStarts
     */
    public boolean isWhiteStarts() {
        return whiteStarts;
    }

    /**
     * @see #timePerSide
     */
    public String getFen() {
        return fen;
    }

    /**
     * @see #timePerSide
     */
    public long getTimePerSide() {
        return timePerSide;
    }

    /**
     * @see #timePerMove
     */
    public long getTimePerMove() {
        return timePerMove;
    }

    /**
     * @see #canPause
     */
    public boolean canPause() {
        return canPause;
    }

    /**
     * @see #canUndo
     */
    public boolean canUndo() {
        return canUndo;
    }

    /**
     * @see #whiteTimerManaged
     */
    public boolean isWhiteTimerManged() {
        return whiteTimerManaged;
    }

    /**
     * @see #blackTimerManaged
     */
    public boolean isBlackTimerManaged() {
        return blackTimerManaged;
    }

    /**
     * Creates a new {@link GameSettings} object from the default starting position.
     * 
     * @param timePerSide       The amount of time each side has in seconds.
     * @param timePerMove       The amount of time each side gains per move in
     *                          seconds.
     * @param canPause          If pausing is allowed.
     * @param canUndo           If undoing and redoing is allowed.
     * @param whiteTimerManaged If white's timer should be automatically started and
     *                          stopped after moves.
     * @param blackTimerManaged If black's timer should be automatically started and
     *                          stopped after moves.
     * @throws Exception If the settings provided are invalid.
     */
    public GameSettings(long timePerSide, long timePerMove, boolean canPause, boolean canUndo,
            boolean whiteTimerManaged, boolean blackTimerManaged) throws Exception {

        this(DEFAULT_FEN, timePerSide, timePerMove, canPause, canUndo,
                whiteTimerManaged, blackTimerManaged);

    }

    /**
     * Creates a new {@link GameSettings} object from the default starting position.
     * 
     * @param FEN               The starting position in FEN notation.
     * @param timePerSide       The amount of time each side has in seconds.
     * @param timePerMove       The amount of time each side gains per move in
     *                          seconds.
     * @param canPause          If pausing is allowed.
     * @param canUndo           If undoing and redoing is allowed.
     * @param whiteTimerManaged If white's timer should be automatically started and
     *                          stopped after moves.
     * @param blackTimerManaged If black's timer should be automatically started and
     *                          stopped after moves.
     * @throws Exception If the settings provided are invalid.
     */
    public GameSettings(String FEN, long timePerSide, long timePerMove, boolean canPause, boolean canUndo,
            boolean whiteTimerManaged, boolean blackTimerManaged)
            throws Exception {

        this.fen = FEN;
        this.whiteStarts = true;
        this.timePerSide = timePerSide <= 0 ? -1 : timePerSide;
        this.timePerMove = timePerMove <= 0 ? -1 : timePerMove;
        this.canPause = canPause;
        this.canUndo = canUndo;
        this.whiteTimerManaged = whiteTimerManaged;
        this.blackTimerManaged = blackTimerManaged;

        if ((!whiteTimerManaged || !blackTimerManaged) && (canPause || canUndo))
            throw new Exception("Invalid settings.");

    }

}
