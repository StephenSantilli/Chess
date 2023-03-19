package game;

public class GameSettings {

    /**
     * The time, in milliseconds, each side has in total. Should be {@code -1} if no
     * time control used.
     */
    private final long timePerSide;

    /**
     * The time, in milliseconds, each side gains per move made. Should be
     * {@code -1} if no time control used, but {@code 0} if no timer added per move.
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
     * @see #manageWhiteTimer
     */
    public boolean isWhiteTimerManged() {
        return whiteTimerManaged;
    }

    /**
     * @see #manageBlackTimer
     */
    public boolean isBlackTimerManaged() {
        return blackTimerManaged;
    }

    public GameSettings(long timePerSide, long timePerMove, boolean canPause, boolean canUndo,
            boolean whiteTimerManaged, boolean blackTimerManaged)
            throws Exception {

        this.timePerSide = timePerSide;
        this.timePerMove = timePerMove;
        this.canPause = canPause;
        this.canUndo = canUndo;
        this.whiteTimerManaged = whiteTimerManaged;
        this.blackTimerManaged = blackTimerManaged;

        if ((!whiteTimerManaged || !blackTimerManaged) && (canPause || canUndo))
            throw new Exception("Invalid settings.");

    }

}
