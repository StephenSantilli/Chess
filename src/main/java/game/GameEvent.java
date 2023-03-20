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

    public static final GameEvent STARTED = new GameEvent(TYPE_STARTED);
    public static final GameEvent OVER = new GameEvent(TYPE_OVER);
    public static final GameEvent DRAW_OFFER = new GameEvent(TYPE_DRAW_OFFER);
    public static final GameEvent MESSAGE = new GameEvent(TYPE_MESSAGE);
    public static final GameEvent IMPORTED = new GameEvent(TYPE_IMPORTED);
    public static final GameEvent PAUSED = new GameEvent(TYPE_PAUSED);
    public static final GameEvent RESUMED = new GameEvent(TYPE_RESUMED);

    private final int type;

    private int prevIndex;
    private int currIndex;

    private Position prev;
    private Position curr;

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

    public GameEvent(int type) {

        this.type = type;

    }

    public GameEvent(int type, int prevIndex, int currIndex, Position prev, Position curr) {

        this.type = type;
        this.prevIndex = prevIndex;
        this.currIndex = currIndex;
        this.prev = prev;
        this.curr = curr;

    }

}
