package game;

/**
 * An event that happened during a {@link Game}.
 */
public class GameEvent {

    /**
     * An enumeration of the different types of events.
     */
    public enum Type {
        MOVE,
        STARTED,
        OVER,
        DRAW_OFFER,
        DRAW_DECLINED,
        MESSAGE,
        IMPORTED,
        PAUSED,
        RESUMED
    }

    /** The type of event this is. */
    private final Type type;

    /** The index of the position before this event. */
    private int prevIndex;

    /** The index of the position after this event. */
    private int currIndex;

    /** The position before this event. */
    private Position prev;

    /** The position after this event. */
    private Position curr;

    /** The move that led to this event. */
    private Move move;

    /** Whether or not the move is because of white. */
    private boolean white;

    /** The chat message associated with this event. */
    private Chat message;

    /**
     * @return {@link #type}
     */
    public Type getType() {
        return type;
    }

    /**
     * @return {@link #prevIndex}
     */
    public int getPrevIndex() {
        return prevIndex;
    }

    /**
     * @return {@link #currIndex}
     */
    public int getCurrIndex() {
        return currIndex;
    }

    /**
     * @return {@link #prev}
     */
    public Position getPrev() {
        return prev;
    }

    /**
     * @return {@link #curr}
     */
    public Position getCurr() {
        return curr;
    }

    /**
     * @return {@link #move}
     */
    public Move getMove() {
        return move;
    }

    /**
     * @return {@link #white}
     */
    public boolean isWhite() {
        return white;
    }

    /**
     * @return {@link #message}
     */
    public Chat getMessage() {
        return message;
    }

    /**
     * Creates a new event.
     * 
     * @param type The type of event this is.
     */
    public GameEvent(Type type) {

        this.type = type;

    }

    /**
     * Creates a new event.
     * 
     * @param message The chat message associated with this event.
     */
    public GameEvent(Chat message) {

        this.type = Type.MESSAGE;
        this.message = message;

    }

    /**
     * Creates a new event.
     * 
     * @param type      The type of event this is.
     * @param prevIndex The index of the position before this event.
     * @param currIndex The index of the position after this event.
     * @param prev      The position before this event.
     * @param curr      The position before this event.
     * @param move      The move that caused this event.
     * @param white     The color that caused this event.
     */
    public GameEvent(Type type, int prevIndex, int currIndex, Position prev, Position curr, Move move, boolean white) {

        this.type = type;
        this.prevIndex = prevIndex;
        this.currIndex = currIndex;
        this.prev = prev;
        this.curr = curr;
        this.move = move;
        this.white = white;

    }

    /**
     * Creates a new event.
     * 
     * @param type  The type of event this is.
     * @param white The color that caused this event.
     */
    public GameEvent(Type type, boolean white) {

        this.type = type;
        this.white = white;

    }

}
