package game;

public class GameEvent {

    public static final int TYPE_MOVE = 0;
    public static final int TYPE_STARTED = 2;
    public static final int TYPE_OVER = 3;
    public static final int TYPE_DRAW_OFFER = 4;
    public static final int TYPE_MESSAGE = 5;
    public static final int TYPE_IMPORTED = 7;
    public static final int TYPE_PAUSED = 8;
    public static final int TYPE_RESUMED = 9;
    public static final int TYPE_DRAW_DECLINED = 10;

    public static final GameEvent STARTED = new GameEvent(TYPE_STARTED);
    public static final GameEvent OVER = new GameEvent(TYPE_OVER);
    public static final GameEvent DRAW_OFFER = new GameEvent(TYPE_DRAW_OFFER);
    public static final GameEvent IMPORTED = new GameEvent(TYPE_IMPORTED);
    public static final GameEvent PAUSED = new GameEvent(TYPE_PAUSED);
    public static final GameEvent RESUMED = new GameEvent(TYPE_RESUMED);

    private final int type;

    private int prevIndex;
    private int currIndex;

    private Position prev;
    private Position curr;

    private Move move;

    private boolean white;

    private Chat message;

	public int getType() {
        return type;
    }

    public int getPrevIndex() {
        return prevIndex;
    }

    public int getCurrIndex() {
        return currIndex;
    }

    public Position getPrev() {
        return prev;
    }

    public Position getCurr() {
        return curr;
    }

    public Move getMove() {
        return move;
    }

    public boolean isWhite() {
        return white;
    }

    public Chat getMessage() {
        return message;
    }

    public GameEvent(int type) {

        this.type = type;

    }

    public GameEvent(Chat message) {

        this.type = TYPE_MESSAGE;
        this.message = message;

    }

    public GameEvent(int type, int prevIndex, int currIndex, Position prev, Position curr, Move move, boolean white) {

        this.type = type;
        this.prevIndex = prevIndex;
        this.currIndex = currIndex;
        this.prev = prev;
        this.curr = curr;
        this.move = move;
        this.white = white;

    }

    public GameEvent(int type, boolean white) {

        this.type = type;
        this.white = white;

    }

}
