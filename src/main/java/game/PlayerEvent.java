package game;

public class PlayerEvent {

    private final boolean white;

    private final int oldPos;

    private final int currentPos;

    public boolean isWhite() {
        return white;
    }

    public int getOldPos() {
        return oldPos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public PlayerEvent(boolean white, int oldPos, int currentPos) {

        this.white = white;
        this.oldPos = oldPos;
        this.currentPos = currentPos;

    }

}
