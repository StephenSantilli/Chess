package game;

import java.util.ArrayList;

public class Player {

    public static final int MAX_NAME_LENGTH = 20;

    public static final String NAME_REGEX = "[A-z0-9!@#$%^&*()_\\-\\+=\"',. ?:\\/\\[\\]\\{\\}]*";

    private String name;

    private Game game;

    private boolean white;

    private int currentPos;

    private ArrayList<PlayerListener> listeners;

    public String getName() {
        return name;
    }

    public Game getGame() {
        return game;
    }

    public boolean isWhite() {
        return white;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    /**
     * Sets the current position the player is viewing and fires
     * {@link #firePositionChanged()}.
     * 
     * @param currentPos The index position to set to.
     * @throws Exception If position is out of bounds.
     */
    public void setCurrentPos(int currentPos) throws Exception {

        if (currentPos < 0 || currentPos >= game.getPositions().size())
            throw new Exception("Position out of bounds.");

        int old = this.currentPos;

        this.currentPos = currentPos;

        firePositionChanged(old, currentPos);

    }

    /**
     * Sets {@link #currentPos} to the last position.
     * 
     * @throws Exception If position is out of bounds.
     */
    public void setToLastPos() throws Exception {

        setCurrentPos(game.getPositions().size() - 1);

    }

    public Player(Game game, boolean white, String name) {
    
        this.game = game;
        this.white = white;
        this.name = name;
    
        this.listeners = new ArrayList<PlayerListener>();

    }

    public boolean isTurn() {
        return game.isWhiteTurn() == white;
    }

    public void makeMove(Square origin, Square destination) throws Exception {

        if (!isTurn())
            throw new Exception("It is not your turn.");

        game.makeMove(origin, destination);

    }

    /**
     * When a promotion move has been made, use this to set the piece that the
     * player chooses to promote to.
     */
    public void setPromote(char pieceType) throws Exception {

        if (!isTurn())
            throw new Exception("It is not your turn.");

        game.setPromo(pieceType);

    }

    public boolean canPause() {
        return game.getSettings().canPause() && !game.isPaused();
    }

    public void pauseGame() throws Exception {

        game.pauseGame();

    }

    public boolean canResume() {
        return game.getSettings().canPause() && game.isPaused();
    }

    public void resumeGame() throws Exception {

        game.resumeGame();

    }

    public boolean canUndo() {

        return game.getSettings().canUndo() && game.getPositions().size() > 1;

    }

    public void undo() throws Exception {

        game.undoMove();

    }

    public boolean canRedo() {

        return game.getSettings().canUndo() && game.getLastPos().getRedo() != null;

    }

    public void redo() throws Exception {

        game.redoMove();

    }

    public ArrayList<Move> getMoves() {

        ArrayList<Move> moves = new ArrayList<>();

        if (currentPos == game.getPositions().size() - 1 && game.isWhiteTurn() == white)
            moves.addAll(game.getLastPos().getMoves());

        return moves;

    }

    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    public void fireBoardUpdate() {

        for (PlayerListener l : listeners) {

            l.onBoardUpdate(new PlayerEvent(white, 0, 0));

        }

        currentPos = game.getPositions().size() - 1;

    }

    public void fireGameOver() {

        for (PlayerListener l : listeners) {

            l.onGameOver(new PlayerEvent(white, 0, 0));

        }

    }

    public void fireChatReceived() {

        for (PlayerListener l : listeners) {

            l.onChatReceived(new PlayerEvent(white, 0, 0));

        }

    }

    public void fireDrawOfferReceived() {

        for (PlayerListener l : listeners) {

            l.onDrawOfferReceived(new PlayerEvent(white, 0, 0));

        }

    }

    public void firePositionChanged(int old, int current) {

        for (PlayerListener l : listeners) {

            l.onPositionChanged(new PlayerEvent(white, old, current));

        }

    }

}
