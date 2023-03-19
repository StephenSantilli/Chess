package game;

public interface PlayerListener {

    /**
     * Event to be fired when the board is updated. Does not fire when {@code currentPos} is
     * changed.
     */
    public void onBoardUpdate(PlayerEvent event);

    /**
     * Event to be fired when the game is marked as over.
     */
    public void onGameOver(PlayerEvent event);

    /**
     * Event to be fired when a chat message is received.
     */
    public void onChatReceived(PlayerEvent event);

    /**
     * Event to be fired when a draw offer is received.
     */
    public void onDrawOfferReceived(PlayerEvent event);

    /**
     * Event to be fired when the position is changed. Will not be fired if position
     * is changed due to a move being made.
     */
    public void onPositionChanged(PlayerEvent event);

}
