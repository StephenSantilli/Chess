package game;

public interface GameListener {

    /**
     * Event to be fired when the board is updated. Does not fire when {@code currentPos} is
     * changed.
     */
    public void onPlayerEvent(GameEvent event);

}
